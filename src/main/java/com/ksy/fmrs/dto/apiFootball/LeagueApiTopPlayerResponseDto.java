package com.ksy.fmrs.dto.apiFootball;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LeagueApiTopPlayerResponseDto {
    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private int results;
    private Paging paging;
    private List<PlayerWrapper> response;

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
    public static class PlayerWrapper {
        private Player player;
        private List<Statistic> statistics;
    }

    @Data
    public static class Player {
        private int id;
        private String name;
        private String firstname;
        private String lastname;
        private int age;
        private Birth birth;
        private String nationality;
        private String height;
        private String weight;
        private boolean injured;
        private String photo;
    }

    @Data
    public static class Birth {
        private String date;
        private String place;
        private String country;
    }

    @Data
    public static class Statistic {
        private Team team;
        private League league;
        private Games games;
        private Substitutes substitutes;
        private Shots shots;
        private Goals goals;
        private Passes passes;
        private Tackles tackles;
        private Duels duels;
        private Dribbles dribbles;
        private Fouls fouls;
        private Cards cards;
        private Penalty penalty;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
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
    public static class Games {
        private Integer appearences;
        private Integer lineups;
        private Integer minutes;
        private Integer number;
        private String position;
        private String rating;
        private boolean captain;
    }

    @Data
    public static class Substitutes {
        @JsonProperty("in")
        private Integer inCount;
        @JsonProperty("out")
        private Integer outCount;
        private Integer bench;
    }

    @Data
    public static class Shots {
        private Integer total;
        private Integer on;
    }

    @Data
    public static class Goals {
        private Integer total;
        private Integer conceded;
        private Integer assists;
        private Integer saves;
    }

    @Data
    public static class Passes {
        private Integer total;
        private Integer key;
        private Integer accuracy;
    }

    @Data
    public static class Tackles {
        private Integer total;
        private Integer blocks;
        private Integer interceptions;
    }

    @Data
    public static class Duels {
        private Integer total;
        private Integer won;
    }

    @Data
    public static class Dribbles {
        private Integer attempts;
        private Integer success;
        private Integer past;
    }

    @Data
    public static class Fouls {
        private Integer drawn;
        private Integer committed;
    }

    @Data
    public static class Cards {
        private Integer yellow;
        private Integer yellowred;
        private Integer red;
    }

    @Data
    public static class Penalty {
        private Integer won;
        private Integer commited;
        private Integer scored;
        private Integer missed;
        private Integer saved;
    }
}
