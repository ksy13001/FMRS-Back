package com.ksy.fmrs.dto.team;

import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class TeamPlayersResponseDto {
    private final List<PlayerDetailsDto> players;
}
