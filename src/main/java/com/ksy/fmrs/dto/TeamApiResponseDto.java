package com.ksy.fmrs.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeamApiResponseDto {
    private String get;
    private ParametersDto parameters;
    private List<Object> errors;  // 오류 배열, 필요에 따라 타입 변경 가능
    private int results;
    private PagingDto paging;
    private List<TeamWrapperDto> response;

    @Data
    public static class ParametersDto {
        private String id;
    }

    @Data
    public static class PagingDto {
        private int current;
        private int total;
    }

    @Data
    public static class TeamWrapperDto {
        private TeamDto team;
        private VenueDto venue;
    }

    @Data
    public static class TeamDto {
        private int id;
        private String name;
        private String code;
        private String country;
        private int founded;
        private boolean national;
        private String logo;
    }

    @Data
    public static class VenueDto {
        private int id;
        private String name;
        private String address;
        private String city;
        private int capacity;
        private String surface;
        private String image;
    }
}
