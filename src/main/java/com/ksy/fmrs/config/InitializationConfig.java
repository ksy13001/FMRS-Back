package com.ksy.fmrs.config;

import com.ksy.fmrs.service.InitializationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class InitializationConfig {

    /**
     * 서버 restart 시 환경 변수 파일에 INITIAL_DATA_INSERT=true 일 경우 서버 실행시 1번 실행됨
     */
    @Bean
    @ConditionalOnProperty(name = "INITIAL_DATA_INSERT", havingValue = "true")
    public ApplicationRunner initializeDataInsert(InitializationService initializationService) {

        return args -> {
            log.info("Initial data insert started-------------------");

            initializationService.saveInitialTeams()
                    .then(initializationService.saveInitialPlayers())
                    .subscribe();
        };
    }

}
