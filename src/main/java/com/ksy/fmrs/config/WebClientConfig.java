package com.ksy.fmrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${api-football.key}")
    private String apiFootballKey;

    @Value("${api-football.host}")
    private String apiFootballHost;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .codecs(configurer ->   // 최대 버퍼 사이즈
                        configurer.defaultCodecs().maxInMemorySize(2*1024 * 1024))
                .build();
    }
}
