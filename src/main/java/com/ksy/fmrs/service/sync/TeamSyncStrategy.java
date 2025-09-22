package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.mapper.ApiFootballMapper;
import com.ksy.fmrs.service.ApiFootballClient;
import com.ksy.fmrs.service.ApiFootballValidator;
import com.ksy.fmrs.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TeamSyncStrategy implements SyncStrategy<League, ApiFootballTeamsByLeague, Team> {

    private final ApiFootballClient apiFootballClient;
    private final ApiFootballMapper apiFootballMapper;
    private final ApiFootballValidator apiFootballValidator;
    private final TeamService teamService;

    @Override
    public SyncType getSyncType() {
        return SyncType.TEAM;
    }

    @Override
    public Integer getSyncApiId(League key) {
        return key.getLeagueApiId();
    }

    @Override
    public List<ApiFootballTeamsByLeague> requestSportsData(League league) {
        return List.of(apiFootballClient
                .requestTeamsByLeague(league.getLeagueApiId(), league.getCurrentSeason()));
    }

    @Override
    public void validate(List<ApiFootballTeamsByLeague> dto) {
        for(ApiFootballTeamsByLeague apiFootballTeamsByLeague : dto){
            apiFootballValidator.validateTeam(apiFootballTeamsByLeague);
        }
    }

    @Override
    public List<Team> transformToTarget(List<ApiFootballTeamsByLeague> dto) {
        return apiFootballMapper.toEntity(dto.getFirst());
    }

    @Override
    public void persist(List<Team> teams, League league) {
        teamService.saveAll(teams, league.getId());
    }
}
