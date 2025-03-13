package com.ksy.fmrs.dto.league;

import com.ksy.fmrs.dto.team.TeamStandingDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LeagueStandingDto {
    private Integer leagueApiId;
    private String leagueName;
    private String leagueLogo;
    private int currentSeason;
    private List<TeamStandingDto> standings;
}
