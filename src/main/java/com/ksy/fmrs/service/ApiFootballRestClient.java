package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.ApiFootballPlan;
import com.ksy.fmrs.domain.enums.UrlEnum;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballSquad;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import io.github.resilience4j.ratelimiter.RateLimiter;
import com.ksy.fmrs.dto.apiFootball.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiFootballRestClient implements ApiFootballClient {
    private final RestClientService restClientService;
    private final RateLimiter rateLimiter;

    @Override
    public ApiFootballLeague requestLeagueByApiId(Integer apiId) {
        return executeWithRateLimit(() -> restClientService.getApiResponse(
                UrlEnum.buildLeagueUrl(apiId),
                ApiFootballLeague.class
        ));
    }

    @Override
    public ApiFootballTeamsByLeague requestTeamsByLeague(Integer leagueApiId, int currentSeason) {
        return executeWithRateLimit(() -> restClientService.getApiResponse(
                UrlEnum.buildTeamsUrlByLeagueApiId(leagueApiId, currentSeason),
                ApiFootballTeamsByLeague.class
        ));
    }

    @Override
    public ApiFootballPlayersStatistics requestPlayersByTeam(Integer teamApiId, int currentSeason, int page) {
        return executeWithRateLimit(() -> restClientService.getApiResponse(
                UrlEnum.buildPlayerStatisticsUrlByTeamApiId(teamApiId, currentSeason, page),
                ApiFootballPlayersStatistics.class
        ));
    }

    @Override
    public ApiFootballSquad requestSquad(Integer teamApiId) {
        return executeWithRateLimit(() -> restClientService.getApiResponse(
                UrlEnum.buildSquadUrl(teamApiId),
                ApiFootballSquad.class
        ));
    }

    @Override
    public ApiFootballPlayersStatistics requestPlayerStatistics(Integer leagueApiId, Integer teamApiId, Integer playerApiId, int currentSeason) {
        return executeWithRateLimit(() -> restClientService.getApiResponse(
                UrlEnum.buildPlayerStatUrl(leagueApiId, teamApiId, playerApiId, currentSeason),
                ApiFootballPlayersStatistics.class
        ));
    }

    @Override
    public ApiFootballTransfers requestTransfers(Integer teamApiId) {
        return executeWithRateLimit(() -> restClientService.getApiResponse(
                UrlEnum.buildTransfersUrl(teamApiId),
                ApiFootballTransfers.class
        ));
    }


    private <T> T executeWithRateLimit(Supplier<T> supplier) {
        return rateLimiter.executeSupplier(supplier);
    }
}
