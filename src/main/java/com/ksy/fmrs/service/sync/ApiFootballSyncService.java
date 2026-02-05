package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.apiFootball.*;
import com.ksy.fmrs.dto.transfer.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApiFootballSyncService implements SportsDataSyncService {
    private final SyncRunner syncRunner;
    private final SyncStrategy<Integer, ApiFootballLeague, League> leagueSyncStrategy;
    private final SyncStrategy<League, ApiFootballTeamsByLeague, Team> teamSyncStrategy;
    private final SyncStrategy<Team, ApiFootballPlayersStatistics, Player> playerSyncStrategy;
    private final SyncStrategy<Team, ApiFootballSquad, Integer> squadSyncStrategy;
    private final SyncStrategy<Team, ApiFootballTransfers, TransferRequestDto> transferSyncStrategy;

    @Override
    public void syncLeagues(List<Integer> leagueApiIds) {
        syncRunner.sync(
                leagueApiIds,
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

    @Override
    public void syncTransfers(List<Team> teams) {
        syncRunner.sync(
                teams,
                transferSyncStrategy
        );
    }

}
