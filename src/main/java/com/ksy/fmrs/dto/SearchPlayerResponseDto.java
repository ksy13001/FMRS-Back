package com.ksy.fmrs.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchPlayerResponseDto {

    private final List<PlayerDetailsResponseDto> players;
}
