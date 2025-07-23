package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.player.PlayerOverviewDto;
import com.ksy.fmrs.dto.player.FmPlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlayerFacadeService {
    private final PlayerService playerService;
    private final PlayerStatService playerStatService;

    public PlayerOverviewDto getPlayerOverview(Long playerId) {
        PlayerDetailsDto playerDetailsDto = playerService.getPlayerDetails(playerId);
        PlayerStatDto playerStatDto = playerStatService.saveAndGetPlayerStat(playerId)
                .orElse(null);
        FmPlayerDetailsDto fmPlayerDetailsDto = playerService.getFmPlayerDetails(playerId)
                .orElse(null);

        return new PlayerOverviewDto(playerDetailsDto, fmPlayerDetailsDto, playerStatDto);
    }
}
