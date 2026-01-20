package com.ridho.adzanreminder.broadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.ridho.adzanreminder.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_WAKTU_SHOLAT = "extra_waktu_sholat";
    private static final String CHANNEL_ID = "adzan_channel";
    private static final int NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        String waktuSholat = intent.getStringExtra(EXTRA_WAKTU_SHOLAT);
        if (waktuSholat != null) {
            tampilkanNotifikasi(context, "Waktunya Sholat " + waktuSholat, "Jangan lupa sholat tepat waktu ya!");
            Toast.makeText(context, "Alarm Sholat " + waktuSholat, Toast.LENGTH_LONG).show();
            
            // Logic to reschedule for tomorrow can be added here if we want robust daily alarms 
            // without app opening, but for now we keep it simple as per request.
        }
    }

    private void tampilkanNotifikasi(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Resource adzan.mp3 must be in res/raw
        Uri adzanSound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.adzan);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) 
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(adzanSound)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Adzan Reminder", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel untuk notifikasi adzan");
            channel.setSound(adzanSound, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
