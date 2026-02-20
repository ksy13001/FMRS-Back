package com.ksy.fmrs.config;

import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.service.FmPlayerImportService;
import com.ksy.fmrs.service.sync.ApiFootballSyncService;
import com.ksy.fmrs.service.sync.SportsDataSyncService;
import com.ksy.fmrs.service.SportsDataSyncServiceWebFlux;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.IntStream;

@Slf4j
@Configuration
public class SportsDataSyncConfig {

    private static final int LAST_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;

    /**
     * 서버 restart 시 환경 변수 파일에 INITIAL_DATA_INSERT=true 일 경우 서버 실행시 1번 실행됨
     */
    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "league")
    public ApplicationRunner initializeLeague(SportsDataSyncService syncService) {
        return args -> {
            log.info("Initial team insert started");
            syncService.syncLeagues(IntStream.rangeClosed(FIRST_LEAGUE_ID, LAST_LEAGUE_ID).boxed().toList());
        };
    }

    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "team")
    public ApplicationRunner initializeClubTeam(ApiFootballSyncService syncService,
                                                LeagueRepository leagueRepository) {
        return args -> {
            log.info("Initial team insert started");
            syncService.syncTeams(leagueRepository.findLeaguesByLeagueType(LeagueType.LEAGUE));
        };
    }


    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player")
    public ApplicationRunner initializePlayer(SportsDataSyncService syncService,
                                              TeamRepository teamRepository) {
        return args -> {
            log.info("Initializing player started");
            syncService.syncPlayers(teamRepository.findAll());
        };
    }

    //    @Bean
//    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "player_from_player_raw")
//    public ApplicationRunner initializePlayerFromPlayerRaw(SportsDataSyncServiceWebFlux initializerService) {
//
//        return args -> {
//            log.info("Initializing player row started");
//            initializerService.initializePlayerFromPlayerRaw();
//        };
//    }


//    @Bean
//    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "append_player")
//    public ApplicationRunner appendNewPlayer(SportsDataSyncServiceWebFlux initializerService,
//                                             LeagueRepository leagueRepository, PlayerRepository playerRepository) {
//
//        return args -> {
//            log.info("appending player started");
//            initializerService.saveInitialPlayers(leagueRepository.findAll(), playerRepository.findAllPlayerApiId()).subscribe();
//        };
//    }

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
    @ConditionalOnProperty(name = "initial.data.insert", havingValue = "fmplayer")
    public ApplicationRunner initializeFMPlayer(FmPlayerImportService fmPlayerImportService,
                                                @Value("${init.fmplayer_dir_path}") String dirPath,
                                                @Value("${fm.version}") String fmVersion) {

        return args -> {
            log.info("Initializing fmplayer started");
            fmPlayerImportService.saveFmPlayers(dirPath, FmVersion.fromString(fmVersion));
        };
    }

}
