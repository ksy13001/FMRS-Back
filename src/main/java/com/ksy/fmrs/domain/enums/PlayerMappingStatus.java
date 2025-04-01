package com.ksy.fmrs.domain.enums;

public enum PlayerMappingStatus {
    MATCHED("MATCHED"),
    UNMAPPED("UNMAPPED"),
    FAILED("FAILED");

    private final String value;

    PlayerMappingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;

    }
}
