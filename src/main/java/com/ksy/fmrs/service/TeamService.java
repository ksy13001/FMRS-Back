package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.TeamDetailsDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    public void saveByTeamDetails(TeamDetailsDto teamDetailsDto) {
        Team team = Team.builder()
                .name(teamDetailsDto.getTeamName())
                .teamApiId(teamDetailsDto.getTeamApiId())
                .logoUrl(teamDetailsDto.getLogoImageUrl())
                .nationName(teamDetailsDto.getNationName())
                .nationLogoImageUrl(teamDetailsDto.getNationLogoImageUrl())
                .currentSeason(teamDetailsDto.getCurrentSeason())
                .build();

        League league = leagueRepository.findLeagueByLeagueApiId(teamDetailsDto.getLeagueApiId())
                .orElseThrow(()-> new RuntimeException("League not found leagueApiId: " + teamDetailsDto.getLeagueApiId()));
        team.updateLeague(league);
        teamRepository.save(team);
    }

}
