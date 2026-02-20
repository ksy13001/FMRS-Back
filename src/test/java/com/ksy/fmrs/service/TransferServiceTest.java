package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.TransferType;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.Transfer;
import com.ksy.fmrs.dto.transfer.TransferRequestDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.repository.TransferRepositoryJDBC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    private static final int TOTAL = 10;
    @InjectMocks
    private TransferService transferService;

    @Mock
    private TransferRepositoryJDBC transferRepositoryJDBC;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Test
    void saveAll_success() {
        // given
        List<TransferRequestDto> dtos = createDtos();
        Set<Integer> playerApiIds = dtos.stream().map(TransferRequestDto::playerApiId).collect(Collectors.toSet());
        Set<Integer> teamApiIds = dtos.stream()
                .flatMap(dto -> Stream.of(dto.fromTeamApiId(), dto.toTeamApiId()))
                .collect(Collectors.toSet());

        List<Player> players = new ArrayList<>();
        List<Team> teams = new ArrayList<>();

        for(int i=0;i<TOTAL;i++){
            players.add(Player.builder().playerApiId(i).build());
            teams.add(Team.builder().teamApiId(i).build());
            teams.add(Team.builder().teamApiId(i+TOTAL).build());
        }

        given(playerRepository.findByPlayerApiIdIn(playerApiIds))
                .willReturn(players);
        given(teamRepository.findByTeamApiIdIn(teamApiIds))
                .willReturn(teams);
        // when
        transferService.saveAll(dtos);

        // then
        ArgumentCaptor<List<Transfer>> captor = ArgumentCaptor.forClass(List.class);
        verify(transferRepositoryJDBC).saveAllOPKU(captor.capture());
        assertThat(captor.getValue()).hasSize(TOTAL);
    }

    @Test
    @DisplayName("playerApiId로 player 찾지 못하면 saveAll은 스킵한다")
    void player_not_found(){
        // given
        given(playerRepository.findByPlayerApiIdIn(anySet()))
                .willReturn(List.of());
        given(teamRepository.findByTeamApiIdIn(anySet()))
                .willReturn(List.of());

        // when && then
        transferService.saveAll(createDtos());
        ArgumentCaptor<List<Transfer>> captor = ArgumentCaptor.forClass(List.class);
        verify(transferRepositoryJDBC).saveAllOPKU(captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    private List<TransferRequestDto> createDtos() {
        List<TransferRequestDto> dtos = new ArrayList<>();
        for (int i = 0; i < TOTAL; i++) {
            dtos.add(new TransferRequestDto(
                    i, i, i + TOTAL, TransferType.PERMANENT, 300d, "EUR", LocalDate.now(), LocalDateTime.now()
            ));
        }
        return dtos;
    }
}
