package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;

public interface ApiFootballClient {
    ApiFootballLeague requestLeagueByApiId(Integer apiId);
    ApiFootballTeamsByLeague requestTeamsByLeague(Integer leagueApiId, int currentSeason);
    ApiFootballPlayersStatistics requestPlayersByTeam(Integer teamApiId, int currentSeason, int page);
}
