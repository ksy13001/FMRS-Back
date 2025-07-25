package com.ksy.fmrs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Primary
@RequiredArgsConstructor
@Service
public class WebClientService{

    private final WebClient webClient;

    // API 공통 호출 메서드
    public <T> Mono<T> getApiResponse(String url, Class<T> responseType) {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType);
    }
}
