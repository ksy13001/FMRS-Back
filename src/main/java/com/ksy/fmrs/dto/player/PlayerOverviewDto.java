package com.ksy.fmrs.dto.player;

// Player 정보 합친 dto
public record PlayerOverviewDto(
        PlayerDetailsDto playerDetailsDto,
        FmPlayerDetailsDto fmPlayerDetailsDto,
        PlayerStatDto playerStatDto) {
}
