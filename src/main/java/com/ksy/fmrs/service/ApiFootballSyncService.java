package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.ApiFootballSquad;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApiFootballSyncService implements SportsDataSyncService {
    private final SyncRunner syncRunner;
    private final SyncStrategy<Integer, ApiFootballLeague, League> leagueSyncStrategy;
    private final SyncStrategy<League, ApiFootballTeamsByLeague, Team> teamSyncStrategy;
    private final SyncStrategy<Team, ApiFootballPlayersStatistics, Player> playerSyncStrategy;
    private final SyncStrategy<Team, ApiFootballSquad, Integer> squadSyncStrategy;
    private static final int LAST_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;

    @Override
    public void syncLeagues() {
        syncRunner.sync(
                IntStream.rangeClosed(FIRST_LEAGUE_ID, LAST_LEAGUE_ID).boxed().toList(),
                leagueSyncStrategy);
    }

    @Override
    public void syncTeams(List<League> leagues) {
        syncRunner.sync(
                leagues,
                teamSyncStrategy
        );
    }

    @Override
    public void syncPlayers(List<Team> teams) {
        syncRunner.sync(
                teams,
                playerSyncStrategy
        );
    }

    @Override
    public void syncSquadPlayers(List<Team> teams) {
        syncRunner.sync(
                teams,
                squadSyncStrategy
        );
    }

}
