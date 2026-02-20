package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.comment.CommentCountResponseDto;
import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerFacadeService {
    private final PlayerService playerService;
    private final PlayerStatService playerStatService;
    private final CommentService commentService;

    public PlayerOverviewDto getPlayerOverview(Long playerId) {
        PlayerDetailsDto playerDetailsDto = playerService.getPlayerDetails(playerId);
        PlayerStatDto playerStatDto = playerStatService.saveAndGetPlayerStat(playerId)
                .orElse(null);
        List<FmPlayerDetailsDto> fmPlayerDetailsDto = playerService.findFmPlayerDetails(playerId)
                .orElse(null);
        CommentCountResponseDto  commentCountResponseDto = commentService.getCommentCountByPlayerId(playerId);

        return new PlayerOverviewDto(playerDetailsDto, fmPlayerDetailsDto, playerStatDto, commentCountResponseDto);
    }
}
