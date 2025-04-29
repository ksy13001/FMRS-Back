package com.ksy.fmrs.dto;

import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import lombok.Getter;

// Player 정보 합친 dto
public record PlayerOverviewDto(
        PlayerDetailsDto playerDetailsDto,
        FmPlayerDetailsDto fmPlayerDetailsDto,
        PlayerStatDto playerStatDto) {
}
