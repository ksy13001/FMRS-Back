package com.ksy.fmrs.domain.enums;

public enum ApiFootballPlan {

    PRO("apiFootballPro"),
    ULTRA("apiFootballUltra"),
    MEGA("apiFootballMega"),;

    ApiFootballPlan(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }
}
