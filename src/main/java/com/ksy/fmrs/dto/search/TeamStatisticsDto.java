package com.ksy.fmrs.dto.search;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TeamStatisticsDto {
    String teamName;
    Integer teamApiId;
    String logoImageUrl;
    Integer leagueApiId;
    String leagueName;
    String leagueLogoImageUrl;
    String nationName;
    String nationLogoImageUrl;
    int currentSeason;

    Integer played;
    Integer wins;
    Integer losses;
    Integer draws;
//    Integer goals;
//    Integer against;
}
