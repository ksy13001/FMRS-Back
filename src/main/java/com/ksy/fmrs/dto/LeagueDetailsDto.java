package com.ksy.fmrs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LeagueDetailsDto {

    String name;
    String country;
    String logoUrl;
    int currentSeason;
    List<TeamStandingDto> standings;
    List<PlayerSimpleDto> topScorers;
    List<PlayerSimpleDto> topAssistants;
}
