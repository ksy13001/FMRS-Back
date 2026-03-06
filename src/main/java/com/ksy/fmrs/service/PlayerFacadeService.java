package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.comment.CommentCountResponseDto;
import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerFacadeService {
    private final PlayerService playerService;
    private final PlayerStatService playerStatService;
    private final CommentService commentService;

    @Transactional(readOnly = true)
    public PlayerOverviewDto getPlayerOverview(Long playerId) {
        Player player = playerService.getPlayerWithAll(playerId);

        PlayerDetailsDto playerDetailsDto = playerService.buildPlayerDetailsDto(player);
        PlayerStatResponse playerStatResponse = playerStatService.buildPlayerStatResponse(player);
        List<FmPlayerDetailsDto> fmPlayerDetailsDto = playerService.buildFmPlayerDetails(player)
                .orElse(null);
        CommentCountResponseDto commentCountResponseDto = commentService.getCommentCountByPlayerId(player.getId());

        return new PlayerOverviewDto(playerDetailsDto, fmPlayerDetailsDto, playerStatResponse, commentCountResponseDto);
    }
}