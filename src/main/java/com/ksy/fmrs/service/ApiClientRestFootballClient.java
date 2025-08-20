package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.UrlEnum;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApiClientRestFootballClient implements ApiFootballClient {
    private final RestClientService restClientService;


    @Override
    public ApiFootballLeague fetchLeagueByApiId(Integer apiId) {
        return restClientService.getApiResponse(
                UrlEnum.buildLeagueUrl(apiId),
                ApiFootballLeague.class
        );
    }

    @Override
    public ApiFootballTeamsByLeague fetchTeamsByLeague(Integer leagueApiId, int currentSeason) {
        return restClientService.getApiResponse(
                UrlEnum.buildTeamsUrlByLeagueApiId(leagueApiId, currentSeason),
                ApiFootballTeamsByLeague.class
        );
    }
}
