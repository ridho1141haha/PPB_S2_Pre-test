package com.ridho.adzanreminder.network;

import com.ridho.adzanreminder.model.JadwalResponse;
import com.ridho.adzanreminder.model.KotaResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("v2/sholat/kota/semua")
    Call<KotaResponse> getSemuaKota();

    @GET("v2/sholat/jadwal/{kota}/{tahun}/{bulan}")
    Call<JadwalResponse> getJadwalSholat(
            @Path("kota") String idKota,
            @Path("tahun") int tahun,
            @Path("bulan") int bulan);
}
