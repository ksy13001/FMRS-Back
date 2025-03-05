package com.ksy.fmrs.domain.enums;

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
}
