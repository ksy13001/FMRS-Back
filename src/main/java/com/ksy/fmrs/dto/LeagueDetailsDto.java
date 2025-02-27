package com.ksy.fmrs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LeagueDetailsDto {
    Integer leagueApiId;
    String name;
    String logoImageUrl;
    String nationName;
    String nationImageUrl;
    int currentSeason;
    List<TeamStandingDto> standings;
    List<PlayerSimpleDto> topScorers;
    List<PlayerSimpleDto> topAssistants;
}
