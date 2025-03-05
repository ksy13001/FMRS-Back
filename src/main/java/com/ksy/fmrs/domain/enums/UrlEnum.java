package com.ksy.fmrs.domain.enums;

public enum UrlEnum {
    TEAM_URL("https://v3.football.api-sports.io/teams?"),
    PLAYER_STAT_URL("https://v3.football.api-sports.io/players?"),
    LEAGUE_URL("https://v3.football.api-sports.io/leagues?"),
    STANDING_URL("https://v3.football.api-sports.io/standings?"),
    TOPSCORERS_URL("https://v3.football.api-sports.io/players/topscorers?"),
    TOPASSISTS_URL("https://v3.football.api-sports.io/players/topassists?"),
    SQUAD_URL("https://v3.football.api-sports.io/players/squads?"),
    TEAM_STATISTICS("https://v3.football.api-sports.io/teams/statistics?"),
    PARAM_NAME("name="),
    PARAM_SEARCH("search="),
    PARAM_TEAM("team="),
    PARAM_SEASON("season="),
    PARAM_ID("id="),
    PARAM_LEAGUE("league="),
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

    // 리그 정보 get API URL 생성(leagueId:Integer) 리그 1172개 전부 조회 할거라 id 값 몰라도됨
    public static String buildLeagueUrl(Integer leagueApiId) {
        return LEAGUE_URL.getValue() +
                PARAM_ID.getValue() + leagueApiId ;
    }

    public static String buildTopScorersUrl(Integer leagueApiId, int currentSeason) {
        return TOPSCORERS_URL.getValue() +
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() + currentSeason;
    }

    public static String buildTopAssistsUrl(Integer leagueApiId, int currentSeason) {
        return TOPASSISTS_URL.getValue() +
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() + currentSeason;
    }

    public static String buildStandingUrl(Integer leagueApiId, int currentSeason) {
        return STANDING_URL.getValue() +
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() +
                currentSeason;
    }

    public static String buildSquadUrl(Integer teamApiId) {
        return SQUAD_URL.getValue() +
                PARAM_TEAM.getValue() + teamApiId;
    }

    public static String buildTeamStatisticsUrl(Integer teamApiId, Integer leagueApiId, int currentSeason) {
        return TEAM_STATISTICS.getValue() +
                PARAM_TEAM.getValue() + teamApiId+
                AND.getValue()+
                PARAM_LEAGUE.getValue() + leagueApiId+
                AND.getValue()+
                PARAM_SEASON.getValue() + currentSeason;
    }
}
//GET /players/topscorers?league=39&season=2019
//GET /players/topassists?league=39&season=2019