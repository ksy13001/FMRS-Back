package com.ksy.fmrs.config;

import com.ksy.fmrs.service.global.InitializationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ApplicationRunner initializeLeague(InitializationService initializationService) {

        return args -> {
            log.info("Initial league insert started");

            initializationService.saveInitialLeague().subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "team")
    public ApplicationRunner initializeTeam(InitializationService initializationService) {

        return args -> {
            log.info("Initial team insert started");
            initializationService.saveInitialTeams().subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_raw")
    public ApplicationRunner initializePlayerRaw(InitializationService initializationService) {

        return args -> {
            log.info("Initial playerRaw insert started");
            initializationService.savePlayerRaws().subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_from_player_raw")
    public ApplicationRunner initializePlayerFromPlayerRaw(InitializationService initializationService) {

        return args -> {
            log.info("Initializing player row started");
            initializationService.initializePlayerFromPlayerRaw();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player")
    public ApplicationRunner initializePlayer(InitializationService initializationService) {

        return args -> {
            log.info("Initializing player started");
            initializationService.saveInitialPlayers().subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "fmplayer")
    public ApplicationRunner initializeFMPlayer(InitializationService initializationService) {

        return args -> {
            log.info("Initializing fmplayer started");
            initializationService.saveFmPlayers(dirPath);
        };
    }

}
