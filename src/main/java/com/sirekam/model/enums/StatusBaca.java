package com.sirekam.model.enums;

public enum StatusBaca {
    TERKIRIM("terkirim"),
    DIBACA("dibaca");

    private final String value;

    StatusBaca(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}