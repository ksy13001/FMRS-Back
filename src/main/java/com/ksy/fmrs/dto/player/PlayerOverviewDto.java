package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.dto.comment.CommentCountResponseDto;

import java.util.List;

// Player 정보 합친 dto
public record PlayerOverviewDto(
        PlayerDetailsDto playerDetailsDto,
        List<FmPlayerDetailsDto> fmPlayerDetailsDto,
        PlayerStatDto playerStatDto,
        CommentCountResponseDto commentCountResponseDto) {
}
