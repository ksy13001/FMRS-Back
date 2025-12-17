package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.UrlEnum;
import com.ksy.fmrs.dto.apiFootball.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiFootballRestClient implements ApiFootballClient {
    private final RestClientService restClientService;

    @Override
    public ApiFootballLeague requestLeagueByApiId(Integer apiId) {
        return restClientService.getApiResponse(
                UrlEnum.buildLeagueUrl(apiId),
                ApiFootballLeague.class
        );
    }

    @Override
    public ApiFootballTeamsByLeague requestTeamsByLeague(Integer leagueApiId, int currentSeason) {
        return restClientService.getApiResponse(
                UrlEnum.buildTeamsUrlByLeagueApiId(leagueApiId, currentSeason),
                ApiFootballTeamsByLeague.class
        );
    }

    @Override
    public ApiFootballPlayersStatistics requestPlayersByTeam(Integer teamApiId, int currentSeason, int page) {
        return restClientService.getApiResponse(
                UrlEnum.buildPlayerStatisticsUrlByTeamApiId(teamApiId, currentSeason, page),
                ApiFootballPlayersStatistics.class
        );
    }

    @Override
    public ApiFootballSquad requestSquad(Integer teamApiId) {
        return restClientService.getApiResponse(
                UrlEnum.buildSquadUrl(teamApiId),
                ApiFootballSquad.class
        );
    }

    @Override
    public ApiFootballPlayersStatistics requestPlayerStatistics(Integer leagueApiId, Integer teamApiId, Integer playerApiId, int currentSeason) {
        return restClientService.getApiResponse(
                UrlEnum.buildPlayerStatUrl(leagueApiId, teamApiId, playerApiId, currentSeason),
                ApiFootballPlayersStatistics.class
        );
    }

    @Override
    public ApiFootballTransfers requestTransfers(Integer teamApiId) {
        return restClientService.getApiResponse(
                UrlEnum.buildTransfersUrl(teamApiId),
                ApiFootballTransfers.class
        );
    }


}
