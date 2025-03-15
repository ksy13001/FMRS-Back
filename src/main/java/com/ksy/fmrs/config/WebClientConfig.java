package com.ksy.fmrs.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${api-football.key}")
    private String apiFootballKey;

    @Value("${api-football.host}")
    private String apiFootballHost;

    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // 연결 타임아웃 5초
            .responseTimeout(Duration.ofSeconds(15))             // 전체 응답 타임아웃 15초
            .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(15))
                            .addHandlerLast(new WriteTimeoutHandler(15))
            );

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer ->   // 최대 버퍼 사이즈
                        configurer.defaultCodecs().maxInMemorySize(2*1024 * 1024))
                .build();
    }
}
