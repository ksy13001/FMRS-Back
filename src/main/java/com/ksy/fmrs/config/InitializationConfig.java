package com.ksy.fmrs.config;

import com.ksy.fmrs.initalizer.DataInitializer;
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
    public ApplicationRunner initializeLeague(DataInitializer dataInitializer,
                                              LeagueRepository leagueRepository) {
        Set<Integer> existLeagueApiIds = leagueRepository.findAllLeagueApiIds();
        return args -> {
            log.info("Initial league insert started. existLeagueApiIds Size={}", existLeagueApiIds.size());
            existLeagueApiIds.stream().forEach(id -> log.info("id:{}", id));
            dataInitializer.saveInitialLeague(existLeagueApiIds)
                    .subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "team")
    public ApplicationRunner initializeTeam(DataInitializer dataInitializer,
                                            LeagueRepository leagueRepository,
                                            TeamRepository teamRepository) {

        return args -> {
            log.info("Initial team insert started");
            dataInitializer.saveInitialTeams(leagueRepository.findAll(), teamRepository.findTeamApiIds())
                    .subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "append_team")
    public ApplicationRunner appendNewTeam(DataInitializer dataInitializer,
                                           LeagueRepository leagueRepository,
                                           TeamRepository teamRepository) {

        return args -> {
            log.info("append New team started");
            dataInitializer.saveInitialTeams(
                            leagueRepository.findUnassignedLeagues(),
                            teamRepository.findTeamApiIds())
                    .subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_raw")
    public ApplicationRunner initializePlayerRaw(DataInitializer dataInitializer) {

        return args -> {
            log.info("Initial playerRaw insert started");
            dataInitializer.savePlayerRaws().subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_from_player_raw")
    public ApplicationRunner initializePlayerFromPlayerRaw(DataInitializer dataInitializer) {

        return args -> {
            log.info("Initializing player row started");
            dataInitializer.initializePlayerFromPlayerRaw();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player")
    public ApplicationRunner initializePlayer(DataInitializer dataInitializer) {

        return args -> {
            log.info("Initializing player started");
            dataInitializer.saveInitialPlayers().subscribe();
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "fmplayer")
    public ApplicationRunner initializeFMPlayer(DataInitializer dataInitializer) {

        return args -> {
            log.info("Initializing fmplayer started");
            dataInitializer.saveFmPlayers(dirPath);
        };
    }

}
