package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.mapper.ApiFootballMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TeamSyncCallback implements SyncCallback<League, ApiFootballTeamsByLeague, Team> {

    private final ApiFootballClient apiFootballClient;
    private final ApiFootballMapper apiFootballMapper;
    private final ApiFootballValidator apiFootballValidator;
    private final TeamService teamService;

    @Override
    public void beforeEach(League league) {
        log.info("league apiId:{}, name:{} 내 팀 조회 시작", league.getLeagueApiId(), league.getName());
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
    public List<Team> transFormToTarget(List<ApiFootballTeamsByLeague> dto) {
        return apiFootballMapper.toEntity(dto.getFirst());
    }

    @Override
    public void persist(List<Team> teams, League league) {
        teamService.saveAll(teams, league.getId());
    }

    @Override
    public void afterEach(League league) {
        log.info("league:{} 내 팀 업데이트 종료", league.getName());
    }
}
