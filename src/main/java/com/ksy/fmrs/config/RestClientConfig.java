package com.ksy.fmrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${api-football.key}")
    private String apiFootballKey;

    @Value("${api-football.host}")
    private String apiFootballHost;

    @Value("${api-football.connect-timeout-ms}")
    private long connectTimeoutMs;

    @Value("${api-football.read-timeout-ms}")
    private long readTimeoutMs;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(apiFootballRequestFactory())
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .build();
    }

    private ClientHttpRequestFactory apiFootballRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .withReadTimeout(Duration.ofMillis(readTimeoutMs));

        return ClientHttpRequestFactoryBuilder.detect().build(settings);
    }

}

