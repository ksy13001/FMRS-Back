package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.dto.comment.CommentCountResponseDto;

// Player 정보 합친 dto
public record PlayerOverviewDto(
        PlayerDetailsDto playerDetailsDto,
        FmPlayerDetailsDto fmPlayerDetailsDto,
        PlayerStatDto playerStatDto,
        CommentCountResponseDto commentCountResponseDto) {
}
