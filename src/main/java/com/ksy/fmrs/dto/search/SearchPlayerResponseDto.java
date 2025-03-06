package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class SearchPlayerResponseDto {

    private final List<PlayerDetailsDto> players;
}
