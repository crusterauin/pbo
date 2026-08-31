package com.sirekam.model.enums;

public enum Role {
    PENDAFTARAN("pendaftaran"),
    DOKTER("dokter"),
    APOTEKER("apoteker");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromValue(String value) {
        for (Role r : Role.values()) {
            if (r.value.equals(value)) {
                return r;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return switch (this) {
            case PENDAFTARAN -> "Petugas Pendaftaran";
            case DOKTER -> "Dokter";
            case APOTEKER -> "Apoteker";
        };
    }
}