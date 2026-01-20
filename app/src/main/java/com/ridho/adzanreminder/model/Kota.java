package com.ridho.adzanreminder.model;

import com.google.gson.annotations.SerializedName;

public class Kota {
    @SerializedName("id")
    private String id;

    @SerializedName("lokasi")
    private String lokasi;

    public String getId() {
        return id;
    }

    public String getLokasi() {
        return lokasi;
    }

    @Override
    public String toString() {
        return lokasi;
    }
}
