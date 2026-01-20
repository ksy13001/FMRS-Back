package com.ksy.fmrs.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FmVersion {
    FM25("FM24"),
    FM26("FM26");

    private final String value;

    FmVersion(String value) {
        this.value = value;
    }

    public static FmVersion fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("FmVersion value is blank");
        }

        String normalized = value.trim();
        return Arrays.stream(FmVersion.values())
                .filter(fmVersion -> fmVersion.value.equalsIgnoreCase(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown fm version: " + value));
    }
}
