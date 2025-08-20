package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;

public interface ApiFootballClient {
    ApiFootballLeague fetchLeagueByApiId(Integer apiId);
    ApiFootballTeamsByLeague fetchTeamsByLeague(Integer leagueApiId, int currentSeason);
}
