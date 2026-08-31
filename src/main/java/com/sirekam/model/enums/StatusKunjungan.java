package com.sirekam.model.enums;

public enum StatusKunjungan {
    MENUNGGU("Menunggu"),
    DIPERIKSA("Diperiksa"),
    SELESAI("Selesai");

    private final String displayName;

    StatusKunjungan(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}