package com.sirekam.model.enums;

public enum StatusResep {
    MENUNGGU("Menunggu"),
    DIPROSES_APOTEKER("Diproses Apoteker"),
    SELESAI("Selesai");

    private final String displayName;

    StatusResep(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}