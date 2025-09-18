package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.mapper.ApiFootballMapper;
import com.ksy.fmrs.service.ApiFootballClient;
import com.ksy.fmrs.service.ApiFootballValidator;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlayerSyncStrategy implements SyncStrategy<Team, ApiFootballPlayersStatistics, Player> {

    private final ApiFootballClient apiFootballClient;
    private final ApiFootballMapper apiFootballMapper;
    private final ApiFootballValidator apiFootballValidator;
    private final PlayerService playerService;

    @Override
    public Integer getSyncApiId(Team key) {
        return key.getTeamApiId();
    }

    @Override
    public List<ApiFootballPlayersStatistics> requestSportsData(Team team) {
        List<ApiFootballPlayersStatistics> dtos = new ArrayList<>();
        League league = team.getLeague();
        ApiFootballPlayersStatistics dto = apiFootballClient.requestPlayersByTeam(
                team.getTeamApiId(), league.getCurrentSeason(), 1);
        dtos.add(dto);

        int current = dto.paging().current();
        int total = dto.paging().total();

        while(current < total){
            current += 1;
            ApiFootballPlayersStatistics next = apiFootballClient.requestPlayersByTeam(
                    team.getTeamApiId(), league.getCurrentSeason(), current);
            dtos.add(next);
        }
        return dtos;
    }

    @Override
    public void validate(List<ApiFootballPlayersStatistics> dto) {
        for(ApiFootballPlayersStatistics apiFootballPlayersStatistics : dto){
            apiFootballValidator.validatePlayerStatistics(apiFootballPlayersStatistics);
        }
    }

    @Override
    public List<Player> transformToTarget(List<ApiFootballPlayersStatistics> dtos) {
        List<Player> players = new ArrayList<>();
        dtos.forEach(dto1 -> players.addAll(apiFootballMapper.toEntity(dto1)));
        return players;
    }

    @Override
    public void persist(List<Player> players, Team team) {
        playerService.saveAll(players);
    }
}
