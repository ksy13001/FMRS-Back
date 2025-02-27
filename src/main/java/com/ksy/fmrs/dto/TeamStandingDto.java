package com.ksy.fmrs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TeamStandingDto {
    Integer rank;
    Integer teamId;
    String teamName;
    String teamLogo;
    Integer played;
    Integer won;
    Integer drawn;
    Integer lost;
    Integer goalsFor;
    Integer goalsAgainst;
    Integer points;
    Integer goalsDifference;
    String description;
    String form;
}
