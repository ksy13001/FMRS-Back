package com.ksy.fmrs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LeagueInfoDto {

    private Integer leagueApiId;
    private String leagueName;
    private String leagueType;
    private Integer currentSeason;
    private boolean Standing;
}
