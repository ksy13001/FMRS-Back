package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;

import java.util.List;

public interface SportsDataSyncService {
    void syncLeagues();
    void syncTeams(List<League> leagues);
    void syncPlayers(List<Team> teams);
    void syncSquadMembers(List<Team> teams);
}
