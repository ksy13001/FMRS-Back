package com.ksy.fmrs.config;

import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.service.InitializationService;
import com.ksy.fmrs.service.ReactiveInitializeService;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Slf4j
@Configuration
public class InitializationConfig {

    @Value("${init.fmplayer_dir_path}")
    private String dirPath;

    /**
     * 서버 restart 시 환경 변수 파일에 INITIAL_DATA_INSERT=true 일 경우 서버 실행시 1번 실행됨
     */
    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "league")
    public ApplicationRunner initializeLeague(ReactiveInitializeService initializerService,
                                              LeagueRepository leagueRepository) {
        Set<Integer> existLeagueApiIds = leagueRepository.findAllLeagueApiIds();
        return args -> {
            log.info("Initial league insert started. existLeagueApiIds Size={}", existLeagueApiIds.size());
            existLeagueApiIds.stream().forEach(id -> log.info("id:{}", id));
            initializerService.saveInitialLeague(existLeagueApiIds)
                    .subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "team")
    public ApplicationRunner initializeTeam(InitializationService initializerService,
                                            LeagueRepository leagueRepository) {
        return args -> {
            log.info("Initial team insert started");
            initializerService.saveInitialTeams(leagueRepository.findAll());
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "append_team")
    public ApplicationRunner appendNewTeam(ReactiveInitializeService initializerService,
                                           LeagueRepository leagueRepository,
                                           TeamRepository teamRepository) {

        return args -> {
            log.info("append New team started");
            initializerService.saveInitialTeams(
                            leagueRepository.findUnassignedLeagues()).subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_raw")
    public ApplicationRunner initializePlayerRaw(ReactiveInitializeService initializerService,
                                                 LeagueRepository leagueRepository) {

        return args -> {
            log.info("Initial playerRaw insert started");
            initializerService.savePlayerRaws(leagueRepository.findAll()).subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_from_player_raw")
    public ApplicationRunner initializePlayerFromPlayerRaw(ReactiveInitializeService initializerService) {

        return args -> {
            log.info("Initializing player row started");
            initializerService.initializePlayerFromPlayerRaw();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player")
    public ApplicationRunner initializePlayer(ReactiveInitializeService initializerService,
                                              LeagueRepository leagueRepository) {

        return args -> {
            log.info("Initializing player started");
            initializerService.saveInitialPlayers(leagueRepository.findAll(), Set.of()).subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "append_player")
    public ApplicationRunner appendNewPlayer(ReactiveInitializeService initializerService,
                                              LeagueRepository leagueRepository, PlayerRepository playerRepository) {

        return args -> {
            log.info("appending player started");
            initializerService.saveInitialPlayers(leagueRepository.findAll(), playerRepository.findAllPlayerApiId()).subscribe();
        };
    }

//    @Bean
//    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "append_player")
//    public ApplicationRunner appendNewPlayer(DataInitializer dataInitializer,
//                                             LeagueRepository leagueRepository) {
//
//        return args -> {
//            log.info("Initializing player started");
//            dataInitializer
//                    .saveInitialPlayers(leagueRepository.findUnassignedLeagues())
//                    .subscribe();
//        };
//    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "fmplayer")
    public ApplicationRunner initializeFMPlayer(ReactiveInitializeService initializerService) {

        return args -> {
            log.info("Initializing fmplayer started");
            initializerService.saveFmPlayers(dirPath);
        };
    }

}
