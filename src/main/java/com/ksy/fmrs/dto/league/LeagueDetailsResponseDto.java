package com.ksy.fmrs.dto.league;

import com.ksy.fmrs.domain.enums.LeagueType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LeagueDetailsResponseDto {

    private String name;
    private Integer leagueApiId;
    private String logoImageUrl;
    private String nationName;
    private String nationImageUrl;
    private LeagueType leagueType;
    private Integer currentSeason;
    private boolean Standing;
}
