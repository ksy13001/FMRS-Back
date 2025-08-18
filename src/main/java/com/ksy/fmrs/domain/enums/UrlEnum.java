package com.ksy.fmrs.domain.enums;

public enum UrlEnum {
    TEAMS_URL("https://v3.football.api-sports.io/teams?"),
    PLAYER_STATISTICS_URL("https://v3.football.api-sports.io/players?"),
    LEAGUE_URL("https://v3.football.api-sports.io/leagues?"),
    STANDING_URL("https://v3.football.api-sports.io/standings?"),
    TOPSCORERS_URL("https://v3.football.api-sports.io/players/topscorers?"),
    TOPASSISTS_URL("https://v3.football.api-sports.io/players/topassists?"),
    SQUAD_URL("https://v3.football.api-sports.io/players/squads?"),
    TEAM_STATISTICS_URL("https://v3.football.api-sports.io/teams/statistics?"),
    PARAM_NAME("name="),
    PARAM_SEARCH("search="),
    PARAM_TEAM("team="),
    PARAM_SEASON("season="),
    PARAM_ID("id="),
    PARAM_LEAGUE("league="),
    PARAM_PAGE("page="),
    PARAM_SCORE("score="),
    PARAM_TEAMS("teams="),
    AND("&");

    private final String value;

    UrlEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 선수 통계 API URL을 생성 (playerName, teamApiId, 시즌)
    public static String buildPlayerStatUrl(Integer playerApiId, Integer teamApiId, Integer leagueApiId, int currentSeason) {
        return PLAYER_STATISTICS_URL.getValue() +
                PARAM_ID.getValue() + playerApiId +
                AND.getValue() +
                PARAM_TEAM.getValue() + teamApiId +
                AND.getValue() +
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() + currentSeason;
    }

    // 팀 API URL을 생성 (teamName)
    public static String buildTeamUrl(String teamName) {
        return TEAMS_URL.getValue() + PARAM_NAME.getValue()
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
        return TEAM_STATISTICS_URL.getValue() +
                PARAM_TEAM.getValue() + teamApiId+
                AND.getValue()+
                PARAM_LEAGUE.getValue() + leagueApiId+
                AND.getValue()+
                PARAM_SEASON.getValue() + currentSeason;
    }

    public static String buildPlayerStatisticsUrlByTeamApiId(Integer teamApiId, Integer leagueApiId, int currentSeason, int page) {
        return PLAYER_STATISTICS_URL.getValue() +
                PARAM_TEAM.getValue() + teamApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() + currentSeason+
                AND.getValue()+
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_PAGE.getValue() + page;
    }

    public static String buildPlayersUrlByLeagueApiId(Integer leagueApiId, int currentSeason, int page) {
        return PLAYER_STATISTICS_URL.getValue() +
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_PAGE.getValue() + page +
                AND.getValue() +
                PARAM_SEASON.getValue() + currentSeason;
    }

    public static String buildTeamsUrlByLeagueApiId(Integer leagueApiId, int currentSeason){
        return TEAMS_URL.getValue() +
                PARAM_LEAGUE.getValue() + leagueApiId +
                AND.getValue() +
                PARAM_SEASON.getValue() + currentSeason;
    }
}
//GET /players/topscorers?league=39&season=2019
//GET /players/topassists?league=39&season=2019