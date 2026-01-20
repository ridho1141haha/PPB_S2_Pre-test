package com.ridho.adzanreminder.model;

import com.google.gson.annotations.SerializedName;

public class Jadwal {
    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("subuh")
    private String subuh;

    @SerializedName("dzuhur")
    private String dzuhur;

    @SerializedName("ashar")
    private String ashar;

    @SerializedName("maghrib")
    private String maghrib;

    @SerializedName("isya")
    private String isya;

    @SerializedName("date")
    private String date;

    public String getTanggal() {
        return tanggal;
    }

    public String getDate() {
        return date;
    }

    public String getSubuh() {
        return subuh;
    }

    public String getDzuhur() {
        return dzuhur;
    }

    public String getAshar() {
        return ashar;
    }

    public String getMaghrib() {
        return maghrib;
    }

    public String getIsya() {
        return isya;
    }
}
