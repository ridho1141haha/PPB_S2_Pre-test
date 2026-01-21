package com.ridho.adzanstatic.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ridho.adzanstatic.R;
import com.ridho.adzanstatic.broadcast.AlarmReceiver;
import com.ridho.adzanstatic.utils.SharedPreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Static Prayer Times (Hardcoded)
    private static final String SUBUH = "04:30";
    private static final String DZUHUR = "12:00";
    private static final String ASHAR = "15:15";
    private static final String MAGHRIB = "18:00";
    private static final String ISYA = "19:15";

    private EditText etLokasi;
    private TextView tvTanggal;
    private View jadwalSubuh, jadwalDzuhur, jadwalAshar, jadwalMaghrib, jadwalIsya;

    private SharedPreferencesUtil prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        prefs = new SharedPreferencesUtil(this);

        // Load saved location
        String savedLokasi = prefs.getString("lokasi");
        if (savedLokasi != null) {
            etLokasi.setText(savedLokasi);
        }

        // Set tanggall hari ini
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        tvTanggal.setText(sdf.format(now.getTime()));

        // Setup jadwal static
        setupJadwalView(jadwalSubuh, "Subuh", SUBUH, prefs.getBoolean("alarm_subuh", true));
        setupJadwalView(jadwalDzuhur, "Dzuhur", DZUHUR, prefs.getBoolean("alarm_dzuhur", true));
        setupJadwalView(jadwalAshar, "Ashar", ASHAR, prefs.getBoolean("alarm_ashar", true));
        setupJadwalView(jadwalMaghrib, "Maghrib", MAGHRIB, prefs.getBoolean("alarm_maghrib", true));
        setupJadwalView(jadwalIsya, "Isya", ISYA, prefs.getBoolean("alarm_isya", true));

        // Simpen lokasi
        etLokasi.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String lokasi = etLokasi.getText().toString().trim();
                if (!lokasi.isEmpty()) {
                    prefs.saveData("lokasi", lokasi);
                }
            }
        });
    }

    private void initViews() {
        etLokasi = findViewById(R.id.et_lokasi);
        tvTanggal = findViewById(R.id.tv_tanggal);

        jadwalSubuh = findViewById(R.id.jadwal_subuh);
        jadwalDzuhur = findViewById(R.id.jadwal_dzuhur);
        jadwalAshar = findViewById(R.id.jadwal_ashar);
        jadwalMaghrib = findViewById(R.id.jadwal_maghrib);
        jadwalIsya = findViewById(R.id.jadwal_isya);
    }

    private void setupJadwalView(View view, String namaWaktu, String defaultJam, boolean alarmOn) {
        TextView tvNamaWaktu = view.findViewById(R.id.tv_nama_waktu);
        EditText etWaktu = view.findViewById(R.id.et_waktu);
        SwitchMaterial switchAlarm = view.findViewById(R.id.switch_alarm);

        tvNamaWaktu.setText(namaWaktu);

        // Load saved time or use default
        String savedTime = prefs.getString("waktu_" + namaWaktu.toLowerCase());
        etWaktu.setText(savedTime != null ? savedTime : defaultJam);

        switchAlarm.setChecked(alarmOn);

        // Save time on focus lost
        etWaktu.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String waktu = etWaktu.getText().toString().trim();
                if (!waktu.isEmpty()) {
                    // Validate time format
                    if (isValidTime(waktu)) {
                        prefs.saveData("waktu_" + namaWaktu.toLowerCase(), waktu);
                        // Reschedule if alarm is on
                        if (switchAlarm.isChecked()) {
                            scheduleAlarm(namaWaktu, waktu);
                        }
                    } else {
                        // Reset to saved or default if invalid
                        etWaktu.setText(savedTime != null ? savedTime : defaultJam);
                        android.widget.Toast.makeText(MainActivity.this,
                                "Format waktu salah! Gunakan HH:MM (00:00 - 23:59)",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.saveData("alarm_" + namaWaktu.toLowerCase(), isChecked);
            String waktu = etWaktu.getText().toString().trim();
            if (isChecked && !waktu.isEmpty() && isValidTime(waktu)) {
                scheduleAlarm(namaWaktu, waktu);
            } else {
                cancelAlarm(namaWaktu);
            }
        });

        // Reschedule alarm if already enabled
        if (alarmOn) {
            String waktu = etWaktu.getText().toString().trim();
            if (!waktu.isEmpty() && isValidTime(waktu)) {
                scheduleAlarm(namaWaktu, waktu);
            }
        }
    }

    private boolean isValidTime(String time) {
        // Check format HH:MM
        if (!time.matches("\\d{1,2}:\\d{2}")) {
            return false;
        }

        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            // Valid 24-hour format: 00:00 - 23:59
            return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
        } catch (Exception e) {
            return false;
        }
    }

    private void scheduleAlarm(String waktuSholat, String jam) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_WAKTU_SHOLAT, waktuSholat);

        int requestCode = getRequestCodeForWaktu(waktuSholat);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            String[] jamMenit = jam.split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(jamMenit[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(jamMenit[1]));
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
                Log.d(TAG, "Alarm scheduled for " + waktuSholat + " at " + jam +
                        " (timestamp: " + calendar.getTimeInMillis() + ")");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling alarm", e);
        }
    }

    private void cancelAlarm(String waktuSholat) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        int requestCode = getRequestCodeForWaktu(waktuSholat);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private int getRequestCodeForWaktu(String waktu) {
        switch (waktu.toLowerCase()) {
            case "subuh":
                return 1;
            case "dzuhur":
                return 2;
            case "ashar":
                return 3;
            case "maghrib":
                return 4;
            case "isya":
                return 5;
            default:
                return 0;
        }
    }
}
