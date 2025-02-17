package com.ksy.fmrs.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlayerStatisticsApiResponseDto {
    private String get;
    private ParametersDto parameters;
    private List<Object> errors;
    private int results;
    private PagingDto paging;
    private List<PlayerWrapperDto> response;

    @Data
    public static class ParametersDto {
        private String id;
        private String season;
    }

    @Data
    public static class PagingDto {
        private int current;
        private int total;
    }

    @Data
    public static class PlayerWrapperDto {
        private PlayerDto player;
        private List<StatisticDto> statistics;
    }

    @Data
    public static class PlayerDto {
        private int id;
        private String name;
        private String firstname;
        private String lastname;
        private int age;
        private BirthDto birth;
        private String nationality;
        private String height;
        private String weight;
        private boolean injured;
        private String photo;
    }

    @Data
    public static class BirthDto {
        private String date;
        private String place;
        private String country;
    }

    @Data
    public static class StatisticDto {
        private TeamDto team;
        private LeagueDto league;
        private GamesDto games;
        private SubstitutesDto substitutes;
        private ShotsDto shots;
        private GoalsDto goals;
        private PassesDto passes;
        private TacklesDto tackles;
        private DuelsDto duels;
        private DribblesDto dribbles;
        private FoulsDto fouls;
        private CardsDto cards;
        private PenaltyDto penalty;

        @Data
        public static class TeamDto {
            private int id;
            private String name;
            private String logo;
        }

        @Data
        public static class LeagueDto {
            private int id;
            private String name;
            private String country;
            private String logo;
            private String flag;
            private int season;
        }

        @Data
        public static class GamesDto {
            private Integer appearences;
            private Integer lineups;
            private Integer minutes;
            private Integer number;
            private String position;
            private String rating;
            private boolean captain;
        }

        @Data
        public static class SubstitutesDto {
            private Integer in;
            private Integer out;
            private Integer bench;
        }

        @Data
        public static class ShotsDto {
            private Integer total;
            private Integer on;
        }

        @Data
        public static class GoalsDto {
            private Integer total;
            private Integer conceded;
            private Integer assists;
            private Integer saves;
        }

        @Data
        public static class PassesDto {
            private Integer total;
            private Integer key;
            private Integer accuracy;
        }

        @Data
        public static class TacklesDto {
            private Integer total;
            private Integer blocks;
            private Integer interceptions;
        }

        @Data
        public static class DuelsDto {
            private Integer total;
            private Integer won;
        }

        @Data
        public static class DribblesDto {
            private Integer attempts;
            private Integer success;
            private Integer past;
        }

        @Data
        public static class FoulsDto {
            private Integer drawn;
            private Integer committed;
        }

        @Data
        public static class CardsDto {
            private Integer yellow;
            private Integer yellowred;
            private Integer red;
        }

        @Data
        public static class PenaltyDto {
            private Integer won;
            private Integer commited;
            private Integer scored;
            private Integer missed;
            private Integer saved;
        }
    }

}
