package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.PlayerDetailsResponse;
import com.ksy.fmrs.dto.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @InjectMocks private PlayerService playerService;
    @Mock private PlayerRepository playerRepository;

    @Test
    @DisplayName("팀에 속한 선수들 조회 테스트")
    void getTeamPlayers(){
        // given
        Long teamId = 1L;
        ArrayList<Player> players = new ArrayList<>();
        Team team1 = createTeam("team1");
        Team team2 = createTeam("team2");
        for(int i = 0; i < 10; i++){
            Player player = createPlayer("player"+i);
            players.add(player);
            player.updateTeam(team1);
        }
//        for(int i = 0; i < 5; i++){
//            Player player = createPlayer("not_player"+i);
//            players.add(player);
//            player.updateTeam(team2);
//        }
        // when
        when(playerRepository.getPlayersByTeamId(teamId)).thenReturn(players);
        TeamPlayersResponseDto actual = playerService.getTeamPlayersByTeamId(teamId);

        // then
        Assertions.assertThat(actual.getPlayers()).hasSize(10);
        verify(playerRepository, times(1)).getPlayersByTeamId(teamId);
    }

    private Team createTeam(String name){
        return Team.builder().name(name).build();
    }

    private Player createPlayer(String name) {
        return Player.builder().name(name).build();
    }
}