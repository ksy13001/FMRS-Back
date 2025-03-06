package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.service.FootballApiService;
import com.ksy.fmrs.service.InitializationService;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AdminController {
    private final PlayerService playerService;
    private final FootballApiService footballApiService;
    private final InitializationService initializationService;

    // 실축스탯 테스트용 api
    @ResponseBody
    @GetMapping("/api/admin/player/{playerId}")
    public PlayerStatDto getPlayerRealFootBallStatTest(@PathVariable Long playerId) {
        PlayerDetailsDto playerDetailsResponseDto = playerService.getPlayerDetails(playerId);
        PlayerStatDto playerRealFootballStatDto = footballApiService.savePlayerRealStat(
                playerDetailsResponseDto.getId(),
                playerDetailsResponseDto.getName(),
                playerDetailsResponseDto.getTeamName()
        );
        return playerRealFootballStatDto;
    }

    @ResponseBody
    @PostMapping("/api/admin/insert-players/{fm_player}")  //fm_player
    public void insertPlayers(@PathVariable String fm_player) {
        playerService.savePlayersFromFmPlayers(fm_player);
    }

    @ResponseBody
    @PostMapping("/api/admin/insert-league-team-data")
    public void insertInitialLeagueTeamData() {
        initializationService.createInitialLeague();
    }
}
