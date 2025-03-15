package com.ksy.fmrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${api-football.key}")
    private String apiFootballKey;

    @Value("${api-football.host}")
    private String apiFootballHost;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .build();
    }


}

