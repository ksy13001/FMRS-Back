package com.ksy.fmrs.dto.apiFootball;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class StandingsAPIResponseDto {
    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private int results;
    private Paging paging;
    private List<ResponseItem> response;

    @Data
    public static class Parameters {
        private String league;
        private String season;
    }

    @Data
    public static class Paging {
        private int current;
        private int total;
    }

    @Data
    public static class ResponseItem {
        private League league;
    }

    @Data
    public static class League {
        private int id;
        private String name;
        private String country;
        private String logo;
        private String flag;
        private int season;
        // standings는 배열 내 배열 구조
        private List<List<Standing>> standings;
    }

    @Data
    public static class Standing {
        private int rank;
        private Team team;
        private int points;
        private int goalsDiff;
        private String group;
        private String form;
        private String status;
        private String description;
        private MatchStats all;
        private MatchStats home;
        private MatchStats away;
        private String update;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
    }

    @Data
    public static class MatchStats {
        private int played;
        private int win;
        private int draw;
        private int lose;
        private Goals goals;
    }

    @Data
    public static class Goals {
        @JsonProperty("for")// for 과 이름 겹칠때 사용
        private int goalsFor;
        private int against;
    }
}
