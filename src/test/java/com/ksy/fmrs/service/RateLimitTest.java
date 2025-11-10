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

class RateLimitTest {
    private ApiFootballRestClient apiFootballRestClient;
    private RestClientService restClientService;

    private RateLimiter rateLimiter;

    private static final int LIMIT_FOR_PERIOD = 7;

    @BeforeEach
    void setUp(){
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(LIMIT_FOR_PERIOD)
                .limitRefreshPeriod(Duration.ofSeconds(1)) // 이 주기마다 새 토큰 보충
                // 토큰 바닥났을때 0.5 초 대기, 대기 후 토큰 안생기면 실패, 테스트시에는 즉시 실패
                .timeoutDuration(Duration.ofMillis(0))
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
    @DisplayName("apiFootballRestClient 는 현재 사용중인 ApiFootballPlan의 허용 가능한 초당 요청 횟수 초과 시," +
            "RequestNotPermitted 예외 발생")
    void rateLimitToApiFootballRestClient(){
        // given
        Integer leagueId = 1;
        int notPermittedCall = 3;
        given(restClientService.getApiResponse(anyString(), eq(ApiFootballLeague.class)))
                .willReturn(createDummyLeague());
        int failed = 0;

        // when
        for (int i = 0; i < LIMIT_FOR_PERIOD+notPermittedCall; i++){
            try {
                apiFootballRestClient.requestLeagueByApiId(leagueId);

            } catch (RequestNotPermitted e){
                failed++;
            }
        }

        // then
        Assertions.assertThat(failed)
                .isEqualTo(notPermittedCall);
    }

    @Test
    @DisplayName("요청횟수가 LIMIT_FOR_PERIOD * 3배일 경우, limitRefreshPeriod 3배")
    void 메서드명() throws Exception{
        // given

        // when

        // then
    }

    private ApiFootballLeague createDummyLeague(){
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
