package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.search.TeamStatisticsDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    @Transactional
    public void saveAllByTeamDetails(List<TeamStatisticsDto> teamStatisticsDto) {
        List<Team> teams = teamStatisticsDto.stream().map(dto->{
            Team team = Team.builder()
                    .name(dto.getTeamName())
                    .teamApiId(dto.getTeamApiId())
                    .logoUrl(dto.getLogoImageUrl())
                    .nationName(dto.getNationName())
                    .nationLogoImageUrl(dto.getNationLogoImageUrl())
                    .currentSeason(dto.getCurrentSeason())
                    .build();
            League league = leagueRepository.findLeagueByLeagueApiId(dto.getLeagueApiId())
                    .orElseThrow(()-> new RuntimeException("League not found leagueApiId: " + dto.getLeagueApiId()));
            team.updateLeague(league);
            return team;
        }).toList();

        teamRepository.saveAll(teams);
    }

    @Transactional
    public void saveAll(List<Team> teams) {
        teamRepository.saveAll(teams);
    }

    public Team findByTeamApiId(Integer teamApiId) {
        return teamRepository.findTeamByTeamApiId(teamApiId)
                .orElseThrow(()-> new RuntimeException("Team not found teamApiId: " + teamApiId));
    }

}
