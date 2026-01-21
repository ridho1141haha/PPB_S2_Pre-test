package com.ridho.adzanstatic.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ridho.adzanstatic.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_WAKTU_SHOLAT = "extra_waktu_sholat";
    public static final String ACTION_STOP_ADZAN = "com.ridho.adzanstatic.STOP_ADZAN";
    private static final String CHANNEL_ID = "adzan_channel";
    private static final int NOTIFICATION_ID = 100;

    private static MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if action is to stop adzan
        if (ACTION_STOP_ADZAN.equals(intent.getAction())) {
            android.util.Log.d("AlarmReceiver", "Stopping adzan...");
            stopAdzan();
            // Cancel notification
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
            return;
        }

        String waktuSholat = intent.getStringExtra(EXTRA_WAKTU_SHOLAT);
        if (waktuSholat == null) {
            waktuSholat = "Sholat";
        }

        android.util.Log.d("AlarmReceiver", "ALARM TRIGGERED for: " + waktuSholat);

        createNotificationChannel(context);
        showNotification(context, waktuSholat);

        // Stop previous adzan if still playing
        stopAdzan();

        mediaPlayer = MediaPlayer.create(context, R.raw.adzan);
        if (mediaPlayer != null) {
            android.util.Log.d("AlarmReceiver", "Playing adzan sound...");
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });
        } else {
            android.util.Log.e("AlarmReceiver", "MediaPlayer failed to create!");
        }
    }

    private static void stopAdzan() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Adzan Reminder",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifikasi pengingat waktu sholat");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context, String waktuSholat) {
        // Create intent for stopping adzan when notification is clicked
        Intent stopIntent = new Intent(context, AlarmReceiver.class);
        stopIntent.setAction(ACTION_STOP_ADZAN);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                context, 999, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Waktu Sholat " + waktuSholat)
                .setContentText("Waktu sholat " + waktuSholat + " telah tiba - Ketuk untuk stop")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(stopPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
