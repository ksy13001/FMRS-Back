package com.ksy.fmrs.domain.enums;

public enum TokenType {
    ACCESS_TOKEN("access_token", 30 * 60),
    REFRESH_TOKEN("refresh_token", 7 * 24 * 60 * 60);

    private final String value;
    private final int exp;

    TokenType(String value, int exp) {
        this.value = value;
        this.exp = exp;
    }

    public String getType() {
        return value;
    }

    public int getExp(){
        return exp;
    }
}
