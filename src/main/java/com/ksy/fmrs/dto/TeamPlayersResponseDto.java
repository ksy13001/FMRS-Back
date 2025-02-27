package com.ksy.fmrs.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeamPlayersResponseDto {
    private final List<PlayerDetailsDto> players;
}
