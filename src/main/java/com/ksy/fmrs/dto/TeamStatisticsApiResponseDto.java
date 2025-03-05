package com.ksy.fmrs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TeamStatisticsApiResponseDto {
    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private int results;
    private Paging paging;
    private Response response;

    @Data
    public static class Parameters {
        private String league;
        private String season;
        private String team;
    }

    @Data
    public static class Paging {
        private int current;
        private int total;
    }

    @Data
    public static class Response {
        private League league;
        private Team team;
        private String form;
        private Fixtures fixtures;
        private Goals goals;
        private Against against;
        private Biggest biggest;
        private CleanSheet clean_sheet;
        private FailedToScore failed_to_score;
        private Penalty penalty;
        private List<Lineup> lineups;
        private Cards cards;
    }

    @Data
    public static class League {
        private int id;
        private String name;
        private String country;
        private String logo;
        private String flag;
        private int season;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
    }

    @Data
    public static class Fixtures {
        private StatDetail played;
        private StatDetail wins;
        private StatDetail draws;
        private StatDetail loses;
    }

    @Data
    public static class StatDetail {
        private int home;
        private int away;
        private int total;
    }

    @Data
    public static class Goals {
        @JsonProperty("for")
        private GoalsDetail goalsFor;
        private GoalsDetail average;
        private Map<String, MinuteStat> minute;
        private Map<String, UnderOver> under_over;
    }

    @Data
    public static class Against {
        private StatDetail total;
        private GoalsDetail average;
        private Map<String, MinuteStat> minute;
        private Map<String, UnderOver> under_over;
    }

    @Data
    public static class GoalsDetail {
        // total: 경우에 따라 객체나 문자열이 있을 수 있으므로 int로 선언
        // 하지만 여기서는 JSON 구조 상 total은 객체, average는 객체 (문자열 값) → 각각 별도 처리
        private StatDetail total; // for goals: total.home, total.away, total.total
        // average는 일반적으로 문자열 값으로 내려옴
        private String home;
        private String away;
        private String totalAverage; // 필드명 중복 피하기 위해
    }

    @Data
    public static class MinuteStat {
        private Integer total;
        private String percentage;
    }

    @Data
    public static class UnderOver {
        private int over;
        private int under;
    }

    @Data
    public static class Biggest {
        private Streak streak;
        private Map<String, String> wins;   // "home": "4-0", "away": "0-3"
        private Map<String, String> loses;  // "home": "0-2", "away": "2-0"
        private GoalsComparison goals;
    }

    @Data
    public static class Streak {
        private int wins;
        private int draws;
        private int loses;
    }

    @Data
    public static class GoalsComparison {
        @JsonProperty("for")
        private Map<String, Integer> goalsFor;    // home, away
        private Map<String, Integer> against;       // home, away
    }

    @Data
    public static class CleanSheet {
        private int home;
        private int away;
        private int total;
    }

    @Data
    public static class FailedToScore {
        private int home;
        private int away;
        private int total;
    }

    @Data
    public static class Penalty {
        private StatWithPercentage scored;
        private StatWithPercentage missed;
        private int total;
    }

    @Data
    public static class StatWithPercentage {
        private int total;
        private String percentage;
    }

    @Data
    public static class Lineup {
        private String formation;
        private int played;
    }

    @Data
    public static class Cards {
        private Map<String, CardStat> yellow;
        private Map<String, CardStat> red;
    }

    @Data
    public static class CardStat {
        private Integer total;
        private String percentage;
    }
}
