package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.apiFootball.*;

public interface ApiFootballClient {
    ApiFootballLeague requestLeagueByApiId(Integer apiId);
    ApiFootballTeamsByLeague requestTeamsByLeague(Integer leagueApiId, int currentSeason);
    ApiFootballPlayersStatistics requestPlayersByTeam(Integer teamApiId, int currentSeason, int page);
    ApiFootballSquad requestSquad(Integer teamApiId);
    ApiFootballPlayersStatistics requestPlayerStatistics( Integer leagueApiId, Integer teamApiId, Integer playerApiId, int currentSeason);
    ApiFootballTransfers requestTransfers(Integer teamApiId);
}
