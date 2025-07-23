package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.team.TeamDetailsDto;
import com.ksy.fmrs.repository.Team.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;
    @Mock
    private TeamRepository teamRepository;

    @Test
    @DisplayName("모든 팀 조회시 이름 순 정렬")
    void findAllTeams(){
        // given
        String teamName = "t";
        Team team1 = Team.builder().name("t1").logoUrl("url1").build();
        Team team2 = Team.builder().name("t2").logoUrl("url2").build();
        Team team3 = Team.builder().name("t3").logoUrl("url3").build();

        // when
        when(teamRepository.findAllByNameStartingWithOrderByNameAsc(teamName))
                .thenReturn(List.of(team1, team2, team3));
        List<TeamDetailsDto> actual = teamService.findTeamsByName(teamName);

        // then
        Assertions.assertThat(actual).extracting(TeamDetailsDto::getTeamName)
                .containsExactly(team1.getName(),team2.getName(),team3.getName());
        Assertions.assertThat(actual).extracting(TeamDetailsDto::getTeamLogo)
                .containsExactlyInAnyOrder(team1.getLogoUrl(),team2.getLogoUrl(),team3.getLogoUrl());
    }
}