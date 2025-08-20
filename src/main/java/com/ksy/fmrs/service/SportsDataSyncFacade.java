package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTeamsByLeague;
import com.ksy.fmrs.mapper.ApiDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SportsDataSyncFacade implements SportsDataSyncService {
    private final FootballApiService footballApiService;
    private final ApiDtoMapper apiDtoMapper;
    private final LeagueService leagueService;
    private final ApiFootballClient apiFootballClient;
    private final TeamService teamService;
    private static final int LAST_LEAGUE_ID = 1172;
    private static final int FIRST_LEAGUE_ID = 1;

    @Override
    public void syncLeagues() {
        for (int nowApiId = FIRST_LEAGUE_ID; nowApiId <= LAST_LEAGUE_ID; nowApiId++) {
            ApiFootballLeague dto = apiFootballClient.fetchLeagueByApiId(nowApiId);
            League league = apiDtoMapper.toEntity(dto);
            leagueService.upsert(league);
        }
    }

    @Override
    public void syncTeams(List<League> leagues) {
        for(League league : leagues){
            log.info("league:{} 내 팀 조회 시작", league.getName());
            ApiFootballTeamsByLeague dto = apiFootballClient
                    .fetchTeamsByLeague(league.getLeagueApiId(), league.getCurrentSeason());
            List<Team> teams = apiDtoMapper.toEntity(dto);
            teamService.SaveAll(teams, league.getId());
            log.info("league:{} 내 팀 업데이트 종료", league.getName());
        }
    }

    private Integer parsingLeagueApiId(ApiFootballTeamsByLeague dto) {
        Integer LeagueApiId = null;
        try {
            LeagueApiId = Integer.valueOf(dto.parameters().league());
        } catch (NullPointerException e) {
            log.error("League api id is null");
        }
        return LeagueApiId;
    }

}
