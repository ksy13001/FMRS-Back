package com.ksy.fmrs.dto;

import lombok.Data;
import java.util.List;

@Data
public class SquadApiResponseDto {
    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private int results;
    private Paging paging;
    private List<ResponseItem> response;

    @Data
    public static class Parameters {
        private String team;
    }

    @Data
    public static class Paging {
        private int current;
        private int total;
    }

    @Data
    public static class ResponseItem {
        private Team team;
        private List<Player> players;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
    }

    @Data
    public static class Player {
        private int id;
        private String name;
        private int age;
        private Integer number;  // null 값 가능
        private String position;
        private String photo;
    }
}
