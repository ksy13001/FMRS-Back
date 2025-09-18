package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.mapper.ApiFootballMapper;
import com.ksy.fmrs.service.ApiFootballClient;
import com.ksy.fmrs.service.ApiFootballValidator;
import com.ksy.fmrs.service.LeagueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LeagueSyncStrategy implements SyncStrategy<Integer, ApiFootballLeague, League> {

    private final ApiFootballClient apiFootballClient;
    private final ApiFootballMapper apiFootballMapper;
    private final LeagueService leagueService;
    private final ApiFootballValidator apiFootballValidator;

    @Override
    public Integer getSyncApiId(Integer key) {
        return key;
    }

    @Override
    public List<ApiFootballLeague> requestSportsData(Integer key) {
        return List.of(apiFootballClient.requestLeagueByApiId(key));
    }

    @Override
    public void validate(List<ApiFootballLeague> dto) {;
        for(ApiFootballLeague apiFootballLeague : dto){
            apiFootballValidator.validateLeague(apiFootballLeague);
        }
    }

    @Override
    public List<League> transformToTarget(List<ApiFootballLeague> dto) {
        return List.of(apiFootballMapper.toEntity(dto.getFirst()));
    }

    @Override
    public void persist(List<League> entities, Integer key) {
        leagueService.upsert(entities.getFirst());
    }
}
