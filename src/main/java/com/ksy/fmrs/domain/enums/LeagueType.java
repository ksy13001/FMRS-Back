package com.ksy.fmrs.domain.enums;

import java.util.Arrays;

public enum LeagueType {
    LEAGUE("League"),
    CUP("Cup");

    private final String value;

    LeagueType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LeagueType fromValue(String value) {
        return Arrays.stream(LeagueType.values())
                .filter(leagueType -> {
                    return leagueType.getValue().equals(value);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown league type: " + value));
    }
}
