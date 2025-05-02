package com.ksy.fmrs.domain.enums;

import lombok.Getter;

@Getter
public enum MappingStatus {
    MATCHED("MATCHED"),
    UNMAPPED("UNMAPPED"),
    FAILED("FAILED");

    private final String value;

    MappingStatus(String value) {
        this.value = value;
    }

}
