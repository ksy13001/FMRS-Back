package com.ksy.fmrs.domain.enums;

public enum UrlEnum {
    TEAM_URL("https://v3.football.api-sports.io/teams?"),
    PLAYER_STAT_URL("https://v3.football.api-sports.io/players?"),
    PARAM_NAME("name="),
    PARAM_SEARCH("search="),
    PARAM_TEAM("team="),
    PARAM_SEASON("season="),
    SEASON_2024_2025("2024"),
    AND("&");

    private final String value;

    UrlEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 선수 통계 API URL을 생성 (playerName, teamApiId, 시즌)
    public static String buildPlayerStatUrl(String playerName, Integer teamApiId) {
        return PLAYER_STAT_URL.getValue() +
                PARAM_SEARCH.getValue() + playerName +
                AND.getValue() +
                PARAM_TEAM.getValue() + teamApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() + SEASON_2024_2025.getValue();
    }

    // 팀 API URL을 생성 (teamName)
    public static String buildTeamUrl(String teamName) {
        return TEAM_URL.getValue() + PARAM_NAME.getValue()
                + teamName;
    }
}