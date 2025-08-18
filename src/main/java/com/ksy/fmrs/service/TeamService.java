package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.TeamListApiResponseDto;
import com.ksy.fmrs.dto.team.TeamDetailsDto;
import com.ksy.fmrs.mapper.ApiDtoMapper;
import com.ksy.fmrs.repository.BulkRepository;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;
    private final ApiDtoMapper apiDtoMapper;
    private final BulkRepository bulkRepository;

    @Transactional(readOnly = true)
    public TeamDetailsDto findTeamById(Long id) {
        return teamRepository.findById(id)
                .map(TeamDetailsDto::new)
                .orElseThrow(()-> new EntityNotFoundException("team not found"));
    }

    @Transactional(readOnly = true)
    public List<TeamDetailsDto> findTeamsByName(String name) {
        return teamRepository
                .findAllByNameStartingWithOrderByNameAsc(name)
                .stream().map(TeamDetailsDto::new)
                .toList();
    }

    @Transactional
    public void saveAllByTeamStanding(TeamListApiResponseDto teamApiResponse, Long leagueId) {
        // 한 팀이 여러 리그에 속하는 케이스 있기 때문에 일단 중복 리그중 하나는 제거(브라질)
//        List<Team> teams = teamApiResponse.stream()
//                .filter(teamStandingDto -> !existTeamApiIds.contains(teamStandingDto.parameters().league()))
//                .map(dto -> {
//                    Team team = apiDtoMapper.toEntity(dto);
//                    Integer leagueApiId = Integer.valueOf(dto.parameters().league());
//                    League league = leagueRepository.findLeagueByLeagueApiId(leagueApiId)
//                    .orElseThrow(()-> new RuntimeException("League not found leagueApiId: " + leagueApiId));
//                    team.updateLeague(league);
//                    return team;
//                    }, (existing, replacement) -> existing
//                ).toList();
        bulkRepository.bulkUpsertTeams(
                getTeamsFromApiFootball(teamApiResponse),
                leagueId);
    }

    private List<Team> getTeamsFromApiFootball(TeamListApiResponseDto teamApiResponse){
        return apiDtoMapper.toEntity(teamApiResponse);
    }

//    @Transactional
//    public void saveAll(List<Team> teams) {
//        teamRepository.saveAll(teams);
//    }
//
//    public Team findByTeamApiId(Integer teamApiId) {
//        return teamRepository.findTeamByTeamApiId(teamApiId)
//                .orElseThrow(() -> new RuntimeException("Team not found teamApiId: " + teamApiId));
//    }
//
//    @Transactional
//    public void updateSquad(Team team, Player player) {
//        player.updateTeam(team);
//    }


}
