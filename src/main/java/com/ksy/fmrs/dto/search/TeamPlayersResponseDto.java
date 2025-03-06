package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class TeamPlayersResponseDto {
    private final List<PlayerDetailsDto> players;
}
