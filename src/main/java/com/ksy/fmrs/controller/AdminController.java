package com.ksy.fmrs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.PlayerOverviewDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.repository.Player.PlayerRawRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.scheduler.PlayerUpdateScheduler;
import com.ksy.fmrs.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final PlayerRawRepository playerRawRepository;
    private final PlayerService playerService;
    private final InitializationService initializationService;
    private final PlayerFacadeService playerFacadeService;
    private final PlayerUpdateScheduler playerUpdateScheduler;

    @ResponseBody
    @GetMapping("/api/admin/players/{playerId}")
    public PlayerOverviewDto getPlayerDetail(@PathVariable Long playerId) {
        return playerFacadeService.getPlayerOverview(playerId);
    }

    @ResponseBody
    @PostMapping("/api/admin/insert/player-data")  //fm_player
    public Mono<Void> insertInitialPlayerData() {
        return initializationService.saveInitialPlayers();
    }

    /**
     *  league 초기 데이터 insert
     * */
    @ResponseBody
    @PostMapping("/api/admin/insert/league-data")
    public  Mono<ResponseEntity<Void>> insertInitialLeagueData() {
        return initializationService.saveInitialLeague()
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * team 초기 데이터 insert
     * */
    @ResponseBody
    @PostMapping("/api/admin/insert/team-data")
    public Mono<ResponseEntity<Void>> insertInitialTeamData() {
        return initializationService.saveInitialTeams()
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * playerRaw 초기 데이터 insert
     * */
    @ResponseBody
    @PostMapping("/api/admin/insert/player-raw-data")
    public Mono<ResponseEntity<Void>> insertInitialPlayerRawData() {
        return initializationService.savePlayerRaws()
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    /**
     * playerRaw 통해서 player 저장
     * */
    @ResponseBody
    @PostMapping("/api/admin/insert/player")
    public void insertInitialPlayer() {
        playerRawRepository.findAll().stream().forEach(playerRaw -> {
            try {
                playerService.savePlayersByPlayerRaw(playerRaw);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * fmPlayer 저장
     **/
    @ResponseBody
    @PostMapping("/api/admin/insert/fm-player")
    public void updateAllPlayerApiIds(@RequestParam("dir") String fm_dir) {
        initializationService.saveFmPlayers(fm_dir);
    }

    @ResponseBody
    @PutMapping("/api/admin/mapping")
    public void updateAllPlayerApiIds() {
        int result = initializationService.updateAllPlayersFmData();
        log.info("updated raws = {}", result);
    }

    @ResponseBody
    @PutMapping("/api/admin/update-mapping-fail")
    public void updateAllPlayers() {
        List<Player> duplicatedPlayers = playerService.getDuplicatePlayers();
        List<Player> duplicatedPlayersWithFmplayers = playerService.getPlayersWithMultipleFmPlayers();
        playerService.updatePlayersMappingStatusToFailed(duplicatedPlayersWithFmplayers);
        playerService.updatePlayersMappingStatusToFailed(duplicatedPlayers);
        int result = initializationService.updateAllPlayersFmData();
        log.info("updated raws = {}", result);
    }

    @ResponseBody
    @PutMapping("/api/admin/update/player-team")
    public void updateAllPlayersTeam() {
        playerUpdateScheduler.updateAllSquad();
    }
}
