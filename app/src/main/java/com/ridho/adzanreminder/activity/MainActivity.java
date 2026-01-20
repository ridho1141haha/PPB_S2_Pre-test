package com.ridho.adzanreminder.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ridho.adzanreminder.R;
import com.ridho.adzanreminder.broadcast.AlarmReceiver;
import com.ridho.adzanreminder.model.Jadwal;
import com.ridho.adzanreminder.model.JadwalResponse;
import com.ridho.adzanreminder.model.Kota;
import com.ridho.adzanreminder.model.KotaResponse;
import com.ridho.adzanreminder.network.ApiService;
import com.ridho.adzanreminder.network.RetrofitClient;
import com.ridho.adzanreminder.utils.SharedPreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Spinner spinnerKota;
    private TextView tvLokasi, tvTanggal;
    private View jadwalSubuh, jadwalDzuhur, jadwalAshar, jadwalMaghrib, jadwalIsya;

    private ApiService apiService;
    private SharedPreferencesUtil prefs;
    private List<Kota> daftarKota = new ArrayList<>();
    private String idKotaTerpilih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        apiService = RetrofitClient.getClient().create(ApiService.class);
        prefs = new SharedPreferencesUtil(this);

        loadKota();

        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kota kota = (Kota) parent.getItemAtPosition(position);
                if (kota != null) {
                    idKotaTerpilih = kota.getId();
                    prefs.saveData("id_kota", idKotaTerpilih);
                    loadJadwalSholat();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initViews() {
        spinnerKota = findViewById(R.id.spinner_kota);
        tvLokasi = findViewById(R.id.tv_lokasi);
        tvTanggal = findViewById(R.id.tv_tanggal);

        jadwalSubuh = findViewById(R.id.jadwal_subuh);
        jadwalDzuhur = findViewById(R.id.jadwal_dzuhur);
        jadwalAshar = findViewById(R.id.jadwal_ashar);
        jadwalMaghrib = findViewById(R.id.jadwal_maghrib);
        jadwalIsya = findViewById(R.id.jadwal_isya);
    }

    private void setupJadwalView(View view, String namaWaktu, String jam, boolean alarmOn) {
        TextView tvNamaWaktu = view.findViewById(R.id.tv_nama_waktu);
        TextView tvWaktu = view.findViewById(R.id.tv_waktu);
        SwitchMaterial switchAlarm = view.findViewById(R.id.switch_alarm);

        tvNamaWaktu.setText(namaWaktu);
        tvWaktu.setText(jam);
        switchAlarm.setChecked(alarmOn);

        switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.saveData("alarm_" + namaWaktu.toLowerCase(), isChecked);
            if (isChecked) {
                scheduleAlarm(namaWaktu, jam);
            } else {
                cancelAlarm(namaWaktu);
            }
        });
    }

    private void loadKota() {
        apiService.getSemuaKota().enqueue(new Callback<KotaResponse>() {
            @Override
            public void onResponse(Call<KotaResponse> call, Response<KotaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    daftarKota = response.body().getData();
                    // urut abjad
                    java.util.Collections.sort(daftarKota,
                            (k1, k2) -> k1.getLokasi().compareToIgnoreCase(k2.getLokasi()));

                    ArrayAdapter<Kota> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, daftarKota);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerKota.setAdapter(adapter);

                    String savedKotaId = prefs.getString("id_kota");
                    if (savedKotaId != null) {
                        for (int i = 0; i < daftarKota.size(); i++) {
                            if (daftarKota.get(i).getId().equals(savedKotaId)) {
                                spinnerKota.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Gagal memuat daftar kota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KotaResponse> call, Throwable t) {
                Log.e(TAG, "Error loadKota: ", t);
                Toast.makeText(MainActivity.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadJadwalSholat() {
        if (idKotaTerpilih == null)
            return;

        Calendar now = Calendar.getInstance();
        int tahun = now.get(Calendar.YEAR);
        int bulan = now.get(Calendar.MONTH) + 1;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String tanggalHariIni = sdf.format(now.getTime());

        apiService.getJadwalSholat(idKotaTerpilih, tahun, bulan).enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Jadwal> listJadwal = response.body().getData().getJadwal();
                    Jadwal jadwalHariIni = null;

                    for (Jadwal data : listJadwal) {
                        // Use date field (YYYY-MM-DD) for reliable comparison
                        if (data.getDate() != null && data.getDate().equals(tanggalHariIni)) {
                            jadwalHariIni = data;
                            break;
                        }
                    }

                    if (jadwalHariIni == null && listJadwal.size() >= now.get(Calendar.DAY_OF_MONTH)) {
                        jadwalHariIni = listJadwal.get(now.get(Calendar.DAY_OF_MONTH) - 1);
                    }

                    if (jadwalHariIni != null) {
                        updateUI(jadwalHariIni);
                    } else {
                        Toast.makeText(MainActivity.this, "Jadwal tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Log.e(TAG, "Error loadJadwal: ", t);
            }
        });
    }

    private void updateUI(Jadwal jadwal) {
        if (spinnerKota.getSelectedItem() != null) {
            Kota k = (Kota) spinnerKota.getSelectedItem();
            tvLokasi.setText(k.getLokasi());
        }
        tvTanggal.setText(jadwal.getTanggal());

        setupJadwalView(jadwalSubuh, "Subuh", jadwal.getSubuh(), prefs.getBoolean("alarm_subuh", true));
        setupJadwalView(jadwalDzuhur, "Dzuhur", jadwal.getDzuhur(), prefs.getBoolean("alarm_dzuhur", true));
        setupJadwalView(jadwalAshar, "Ashar", jadwal.getAshar(), prefs.getBoolean("alarm_ashar", true));
        setupJadwalView(jadwalMaghrib, "Maghrib", jadwal.getMaghrib(), prefs.getBoolean("alarm_maghrib", true));
        setupJadwalView(jadwalIsya, "Isya", jadwal.getIsya(), prefs.getBoolean("alarm_isya", true));

        // Reschedule alarms immediately
        rescheduleAllActiveAlarms(jadwal);
    }

    private void rescheduleAllActiveAlarms(Jadwal jadwal) {
        if (prefs.getBoolean("alarm_subuh", true))
            scheduleAlarm("Subuh", jadwal.getSubuh());
        if (prefs.getBoolean("alarm_dzuhur", true))
            scheduleAlarm("Dzuhur", jadwal.getDzuhur());
        if (prefs.getBoolean("alarm_ashar", true))
            scheduleAlarm("Ashar", jadwal.getAshar());
        if (prefs.getBoolean("alarm_maghrib", true))
            scheduleAlarm("Maghrib", jadwal.getMaghrib());
        if (prefs.getBoolean("alarm_isya", true))
            scheduleAlarm("Isya", jadwal.getIsya());
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
