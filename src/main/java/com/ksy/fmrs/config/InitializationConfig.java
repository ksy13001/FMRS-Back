package com.ksy.fmrs.config;

import com.ksy.fmrs.service.InitializationService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitializationConfig {

    /**
     * 서버 restart 시 환경 변수 Environment="FMRS_INIT_PLAYER_RAW=true" 일 경우 서버 실행시 1번 실행됨
     *
     *
     */
    @Bean
    @ConditionalOnProperty(name = "initial-data-insert", havingValue = "true")
    public ApplicationRunner initializeDataInsert(InitializationService initializationService) {

        return args -> {
            initializationService.saveInitialLeague()
                    .then(initializationService.saveInitialTeams())
                    .then(initializationService.saveInitialPlayers())
            .subscribe();
        };
    }

}
