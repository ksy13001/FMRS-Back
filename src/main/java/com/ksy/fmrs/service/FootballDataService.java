package com.ksy.fmrs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FootballDataService {

    private final RestTemplate restTemplate;

    @Value("${football-data.api.base-url}")
    private String url;

    @Value("${football-data.api.token}")
    private String token;
}
