package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;

import java.util.List;

public interface SportsDataSyncService {
    void syncLeagues(List<Integer> leagueApiIds);
    void syncTeams(List<League> leagues);
    void syncPlayers(List<Team> teams);
    void syncSquadPlayers(List<Team> teams);
}
