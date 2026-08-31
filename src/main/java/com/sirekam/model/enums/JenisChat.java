package com.sirekam.model.enums;

public enum JenisChat {
    PETUGAS_DOKTER("petugas_dokter"),
    DOKTER_APOTEKER("dokter_apoteker");

    private final String value;

    JenisChat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}