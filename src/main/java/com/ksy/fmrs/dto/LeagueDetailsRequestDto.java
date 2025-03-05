package com.ksy.fmrs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LeagueDetailsRequestDto {

    private Integer leagueApiId;
    private String leagueName;
    private String logoImageUrl;
    private String nationName;
    private String nationImageUrl;
    private String leagueType;
    private Integer currentSeason;
    private Boolean Standing;
}
