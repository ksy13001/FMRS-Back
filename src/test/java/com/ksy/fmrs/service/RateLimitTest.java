package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RateLimitTest {
    private ApiFootballRestClient apiFootballRestClient;
    private RestClientService restClientService;

    private RateLimiter rateLimiter;

    private static final int LIMIT_FOR_PERIOD = 7;
    private static final Duration REFRESH_PERIOD = Duration.ofSeconds(1);
    private static final Duration TIMEOUT_DURATION = Duration.ofMillis(500);

    @BeforeEach
    void setUp() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(LIMIT_FOR_PERIOD)
                .limitRefreshPeriod(REFRESH_PERIOD) // 이 주기마다 새 토큰 보충
                // 토큰이 없으면 최대 500ms 대기 후 permit 획득 실패 시 예외
                .timeoutDuration(TIMEOUT_DURATION)
                .build();
        this.restClientService = Mockito.mock(RestClientService.class);
        this.rateLimiter = RateLimiter.of(
                "testRateLimiter",
                config
        );
        this.apiFootballRestClient = new ApiFootballRestClient(
                restClientService,
                rateLimiter
        );
    }

    @Test
    @DisplayName("요청이 limit를 초과하면 timeoutDuration(500ms) 대기 후 RequestNotPermitted 예외 발생")
    void shouldThrowRequestNotPermittedAfterWaitingTimeoutDurationWhenExceedingLimit() {
        // given
        Integer leagueId = 1;
        given(restClientService.getApiResponse(anyString(), eq(ApiFootballLeague.class)))
                .willReturn(createDummyLeague());

        for (int i = 0; i < LIMIT_FOR_PERIOD; i++) {
            apiFootballRestClient.requestLeagueByApiId(leagueId);
        }

        long startedAt = System.nanoTime();

        // when
        assertThrows(RequestNotPermitted.class, () ->
                apiFootballRestClient.requestLeagueByApiId(leagueId));

        long elapsedMillis = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();

        // then
        Assertions.assertThat(elapsedMillis)
                .isGreaterThanOrEqualTo(TIMEOUT_DURATION.toMillis() - 50);
    }


    @Test
    @DisplayName("refresh period 경과 후 요청이 다시 허용된다")
    void shouldAllowRequestsAgainAfterRefreshPeriod() throws Exception {
        // given
        Integer leagueId = 1;
        given(restClientService.getApiResponse(anyString(), eq(ApiFootballLeague.class)))
                .willReturn(createDummyLeague());

        for (int i = 0; i < LIMIT_FOR_PERIOD; i++) {
            apiFootballRestClient.requestLeagueByApiId(leagueId);
        }

        Thread.sleep(REFRESH_PERIOD.plusMillis(100).toMillis());

        // when
        ApiFootballLeague response = apiFootballRestClient.requestLeagueByApiId(leagueId);

        // then
        Assertions.assertThat(response).isNotNull();
    }

    private ApiFootballLeague createDummyLeague() {
        return new ApiFootballLeague(
                null,
                null,
                null,
                0,
                null,
                null
        );
    }
}
