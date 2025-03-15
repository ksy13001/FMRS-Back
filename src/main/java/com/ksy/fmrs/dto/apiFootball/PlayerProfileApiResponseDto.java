package com.ksy.fmrs.dto.apiFootball;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerProfileApiResponseDto {

    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private int results;
    private Paging paging;
    private List<ResponseItem> response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parameters {
        private String player;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Paging {
        private int current;
        private int total;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseItem {
        private PlayerDto player;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
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
        private int number;
        private String position;
        private String photo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BirthDto {
        private String date;
        private String place;
        private String country;
    }
}
