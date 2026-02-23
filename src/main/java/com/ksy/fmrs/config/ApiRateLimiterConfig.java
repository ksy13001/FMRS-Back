package com.ksy.fmrs.config;

import com.ksy.fmrs.domain.enums.ApiFootballPlan;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ApiRateLimiterConfig {

    @Value("${api-football.plan}")
    private ApiFootballPlan apiFootballPlan;

    // registry 는 name 으로 프로퍼티에서 값 식별
    // RateLimiterConfig 대신 프로퍼티에서 값 설정
    @Bean
    public RateLimiter ApiFootballRateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        return rateLimiterRegistry.rateLimiter(apiFootballPlan.getValue());
    }
}
