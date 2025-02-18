package com.ksy.fmrs.dto;

import lombok.Data;

@Data
public class PlayerRealFootballStatDto {
    private Integer gamesPlayed;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;
    private String imageUrl;
}
