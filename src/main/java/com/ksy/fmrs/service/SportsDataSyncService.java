package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;

import java.util.List;

public interface SportsDataSyncService {
    void syncLeagues();
    void syncTeams(List<League> leagues);
}
