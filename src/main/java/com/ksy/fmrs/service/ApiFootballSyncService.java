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
    private final SyncTemplate syncTemplate;
    @Qualifier("leagueSyncCallback")
    private final SyncCallback<Integer, ApiFootballLeague, League> leagueSyncCallback;
    @Qualifier("teamSyncCallback")
    private final SyncCallback<League, ApiFootballTeamsByLeague, Team> teamSyncCallback;
    @Qualifier("playerSyncCallback")
    private final SyncCallback<Team, ApiFootballPlayersStatistics, Player> playerSyncCallback;
    @Qualifier("squadSyncCallback")
    private final SyncCallback<Team, ApiFootballSquad, Integer> squadSyncCallback;
    private static final int LAST_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;

    @Override
    public void syncLeagues() {
        syncTemplate.sync(
                IntStream.rangeClosed(FIRST_LEAGUE_ID, LAST_LEAGUE_ID).boxed().toList(),
                leagueSyncCallback);
    }

    @Override
    public void syncTeams(List<League> leagues) {
        syncTemplate.sync(
                leagues,
                teamSyncCallback
        );
    }

    @Override
    public void syncPlayers(List<Team> teams) {
        syncTemplate.sync(
                teams,
                playerSyncCallback
        );
    }

    @Override
    public void syncSquadPlayers(List<Team> teams) {
        syncTemplate.sync(
                teams,
                squadSyncCallback
        );
    }

}
