package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.service.FootballApiService;
import com.ksy.fmrs.service.InitializationService;
import com.ksy.fmrs.service.PlayerService;
import com.ksy.fmrs.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class AdminController {
    private final PlayerService playerService;
    private final FootballApiService footballApiService;
    private final InitializationService initializationService;
    private final SchedulerService  schedulerService;
    private static final int LAST_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;

    // 실축스탯 테스트용 api
    @ResponseBody
    @GetMapping("/api/admin/player/{playerId}")
    public PlayerStatDto getPlayerRealFootBallStatTest(@PathVariable Long playerId) {
        PlayerDetailsDto playerDetailsResponseDto = playerService.getPlayerDetails(playerId);
        return footballApiService.savePlayerRealStat(
                playerDetailsResponseDto.getId(),
                playerDetailsResponseDto.getPlayerApiId()
        );
    }

//    @ResponseBody
//    @PostMapping("/api/admin/insert/players/{fm_player}")  //fm_player
//    public void insertPlayers(@PathVariable String fm_player) {
//        initializationService.savePlayersFromFmPlayers(fm_player);
//    }

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
     * player - fmPlayer 매핑
     **/
    @ResponseBody
    @PostMapping("/api/admin/update/player-fm/{fm_dir}")
    public void updateAllPlayerApiIds(@PathVariable String fm_dir) {
        initializationService.updatePlayerFmStat(initializationService.getPlayersFromFmPlayers(fm_dir));
    }

//    @ResponseBody
//    @PostMapping("/api/admin/update-squad")
//    public void updateSquad() {
//        schedulerService.updateSquad();
//    }
}
