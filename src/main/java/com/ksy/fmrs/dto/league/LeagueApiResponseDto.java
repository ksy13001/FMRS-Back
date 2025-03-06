package com.ksy.fmrs.dto.league;

import lombok.Data;
import java.util.List;

@Data
public class LeagueApiResponseDto {
    private String get;
    private Parameters parameters;
    private List<Object> errors; // errors가 빈 배열 또는 다른 형태일 수 있으므로 유연하게 처리
    private int results;
    private Paging paging;
    private List<ResponseItem> response;

    @Data
    public static class Parameters {
        private String id;
    }

    @Data
    public static class Paging {
        private int current;
        private int total;
    }

    @Data
    public static class ResponseItem {
        private League league;
        private Country country;
        private List<Season> seasons;
    }

    @Data
    public static class League {
        private int id;
        private String name;
        private String type;
        private String logo;
    }

    @Data
    public static class Country {
        private String name;
        private String code;
        private String flag;
    }

    @Data
    public static class Season {
        private int year;
        private String start;
        private String end;
        private boolean current;
        private Coverage coverage;
    }

    @Data
    public static class Coverage {
        private Fixtures fixtures;
        private boolean standings;
        private boolean players;
        private boolean top_scorers;
        private boolean top_assists;
        private boolean top_cards;
        private boolean injuries;
        private boolean predictions;
        private boolean odds;
    }

    @Data
    public static class Fixtures {
        private boolean events;
        private boolean lineups;
        private boolean statistics_fixtures;
        private boolean statistics_players;
    }
}
