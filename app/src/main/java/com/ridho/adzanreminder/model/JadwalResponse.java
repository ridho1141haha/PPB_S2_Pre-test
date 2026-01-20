package com.ridho.adzanreminder.model;

import java.util.List;

public class JadwalResponse {
    private boolean status;
    private DataJadwal data;

    public boolean isStatus() {
        return status;
    }

    public DataJadwal getData() {
        return data;
    }

    public static class DataJadwal {
        private String id;
        private String lokasi;
        private String daerah;
        private List<Jadwal> jadwal;

        public String getId() {
            return id;
        }

        public String getLokasi() {
            return lokasi;
        }

        public String getDaerah() {
            return daerah;
        }

        public List<Jadwal> getJadwal() {
            return jadwal;
        }
    }
}
