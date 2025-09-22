package com.ksy.fmrs.service.sync;


import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.dto.apiFootball.ApiFootballSquad;
import com.ksy.fmrs.service.ApiFootballRestClient;
import com.ksy.fmrs.service.ApiFootballValidator;
import com.ksy.fmrs.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SquadSyncStrategy implements SyncStrategy<Team, ApiFootballSquad, Integer> {

    private final ApiFootballValidator apiFootballValidator;
    private final ApiFootballRestClient apiFootballRestClient;
    private final PlayerService playerService;

    @Override
    public SyncType getSyncType() {
        return SyncType.SQUAD;
    }

    @Override
    public Integer getSyncApiId(Team key) {
        return key.getTeamApiId();
    }

    @Override
    public List<ApiFootballSquad> requestSportsData(Team key) {
        return List.of(apiFootballRestClient.requestSquad(key.getTeamApiId()));
    }

    @Override
    public void validate(List<ApiFootballSquad> dto) {
        apiFootballValidator.validateSquad(dto.getFirst());
    }

    @Override
    public List<Integer> transformToTarget(List<ApiFootballSquad> dto) {
        return dto.getFirst()
                .response()
                .getFirst()
                .players()
                .stream()
                .map(ApiFootballSquad.Player::id)
                .toList();
    }

    @Override
    public void persist(List<Integer> entities, Team key) {
        playerService.updateSquadPlayers(entities, key.getId());
    }
}
