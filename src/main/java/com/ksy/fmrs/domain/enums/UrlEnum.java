package com.ksy.fmrs.domain.enums;

public enum UrlEnum {
    TEAM_URL("https://v3.football.api-sports.io/teams?"),
    PLAYER_STAT_URL("https://v3.football.api-sports.io/players?"),
    PARAM_NAME("name="),
    PARAM_SEARCH("search="),
    PARAM_TEAM("team="),
    AND("&");

    private final String value;

    UrlEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}