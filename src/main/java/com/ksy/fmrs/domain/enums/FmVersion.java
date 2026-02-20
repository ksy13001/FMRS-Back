package com.ksy.fmrs.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;

@Getter
public enum FmVersion {
    FM24("FM24", 2024),
    FM26("FM26", 2026);

    private final String value;
    private final int year;

    FmVersion(String value, int year) {
        this.value = value;
        this.year = year;
    }

    public static FmVersion latest() {
        return Arrays.stream(values())
                .max(Comparator.comparingInt(FmVersion::getYear))
                .orElseThrow();
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
