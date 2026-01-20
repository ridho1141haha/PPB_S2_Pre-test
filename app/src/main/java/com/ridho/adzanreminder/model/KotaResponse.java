package com.ridho.adzanreminder.model;

import java.util.List;

public class KotaResponse {
    private boolean status;
    private List<Kota> data;

    public boolean isStatus() {
        return status;
    }

    public List<Kota> getData() {
        return data;
    }
}
