package com.ksy.fmrs.dto.league;

import com.ksy.fmrs.dto.search.TeamStandingDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LeagueStandingDto {
    private List<TeamStandingDto> standings;
}
