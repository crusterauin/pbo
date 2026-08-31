package com.sirekam.model.enums;

public enum JenisKelamin {
    L("Laki-laki"),
    P("Perempuan");

    private final String displayName;

    JenisKelamin(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}