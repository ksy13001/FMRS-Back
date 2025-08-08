package com.ksy.fmrs.dto.league;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class LeagueAPIDetailsResponseDto {

    private Integer leagueApiId;
    private String leagueName;
    private String logoImageUrl;
    private String nationName;
    private String nationImageUrl;
    private String leagueType;
    private Integer currentSeason;
    private Boolean Standing;
    private LocalDate startDate;
    private LocalDate endDate;
}
