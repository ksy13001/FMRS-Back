package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticApiDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.mapper.PlayerStatMapper;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import com.ksy.fmrs.service.global.FootballApiService;
import com.ksy.fmrs.service.player.PlayerStatService;
import com.ksy.fmrs.util.time.TestTimeProvider;
import com.ksy.fmrs.util.time.TimeProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerStatServiceTest {
    @InjectMocks
    private PlayerStatService playerStatService;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerStatRepository playerStatRepository;
    @Mock
    private FootballApiService footballApiService;
    @Mock
    private PlayerStatMapper playerStatMapper;
    @Spy
    private TimeProvider timeProvider =
            new TestTimeProvider(
                    LocalDateTime.of(2000, 8, 14, 0, 0),
                    LocalDate.of(2000, 8, 14)
            );

    @Test
    @DisplayName("PlayerStat 이 없는 경우에 Player 조회하면 " +
            "PlayerStat을 외부Api 통해서 저장하고 PlayerStat 반환")
    void saveAndGetPlayerStat_playerStatIsNotExist() {
        // given
        Player player = Player.builder()
                .playerApiId(1)
                .build();
        Team team = Team.builder()
                .name("Team")
                .teamApiId(1)
                .build();
        League league = League.builder()
                .name("League")
                .currentSeason(2024)
                .leagueApiId(1)
                .build();
        PlayerStatisticApiDto playerStatisticApiDto = createPlayerStatisticApiDto();
        team.updateLeague(league);
        player.updateTeam(team);
        ReflectionTestUtils.setField(player, "id", 1L);
        ReflectionTestUtils.setField(team, "id", 1L);
        ReflectionTestUtils.setField(league, "id", 1L);

        PlayerStat playerStat = PlayerStat.builder()
                .gamesPlayed(10)
                .goal(5)
                .build();
        // when
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(footballApiService.getPlayerStatByPlayerApiIdAndTeamApiIdAndLeagueApiId(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(playerStatisticApiDto);
        when(playerStatMapper.toEntity(any())).thenReturn(playerStat);

        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(player.getId());
        // then
        verify(playerStatRepository, times(1)).save(playerStat);
        verify(footballApiService).getPlayerStatByPlayerApiIdAndTeamApiIdAndLeagueApiId(
                player.getPlayerApiId(), team.getTeamApiId(),
                league.getLeagueApiId(), league.getCurrentSeason()
        );
        verify(playerStatMapper).toEntity(playerStatisticApiDto);
        Assertions.assertThat(actual.get().getGamesPlayed()).isEqualTo(10);
    }

    @Test
    @DisplayName("PlayerStat 이 존재 하지만 유효기간이 지난경우" +
            "PlayerStat 저장")
    void savePlayerStat_existInvalidPlayerStat() {
        // given
        Player player = Player.builder()
                .playerApiId(1)
                .build();
        Team team = Team.builder()
                .name("Team")
                .teamApiId(1)
                .build();
        League league = League.builder()
                .name("League")
                .currentSeason(2024)
                .leagueApiId(1)
                .build();
        Long playerId = 1L;
        PlayerStatisticApiDto playerStatisticApiDto = createPlayerStatisticApiDto();

        PlayerStat existingStat = PlayerStat.builder()
                .gamesPlayed(10)
                .build();
        ReflectionTestUtils.setField(player, "id", playerId);
        ReflectionTestUtils.setField(existingStat, "modifiedDate",
                LocalDateTime.of(1999, 10, 11, 0, 0));
        PlayerStat refreshStat = PlayerStat.builder()
                .gamesPlayed(12)
                .build();

        player.updatePlayerStat(existingStat);
        team.updateLeague(league);
        player.updateTeam(team);

        // when
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(footballApiService.getPlayerStatByPlayerApiIdAndTeamApiIdAndLeagueApiId(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(playerStatisticApiDto);
        when(playerStatMapper.toEntity(any())).thenReturn(refreshStat);
        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(playerId);
        // then
        verify(playerRepository, times(1)).findById(playerId);
        verify(playerStatRepository, times(1)).save(refreshStat);
        verify(footballApiService).getPlayerStatByPlayerApiIdAndTeamApiIdAndLeagueApiId(
                player.getPlayerApiId(), team.getTeamApiId(),
                league.getLeagueApiId(), league.getCurrentSeason()
        );

        Assertions.assertThat(actual.get().getGamesPlayed()).isEqualTo(12);
    }

    @Test
    @DisplayName("PlayerStat 이 존재 하며 유효기간이 지나지않은 경우" +
            "저장된 PlayerStat 반환, footballApiService 동작 없어야함")
    void savePlayerStat_existValidPlayerStat() {
        // given
        Long playerId = 1L;
        Player player = Player.builder()
                .playerApiId(1)
                .build();
        Team team = Team.builder()
                .name("Team")
                .teamApiId(1)
                .build();
        League league = League.builder()
                .name("League")
                .currentSeason(2024)
                .leagueApiId(1)
                .build();

        PlayerStat existingStat = PlayerStat.builder()
                .gamesPlayed(10)
                .build();
        ReflectionTestUtils.setField(player, "id", playerId);
        ReflectionTestUtils.setField(existingStat, "modifiedDate",
                LocalDateTime.of(2000, 8, 14, 10, 0));

        player.updatePlayerStat(existingStat);
        team.updateLeague(league);
        player.updateTeam(team);

        // when
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(playerId);
        // then
        verify(playerRepository, times(1)).findById(playerId);
        verifyNoInteractions(footballApiService);

        Assertions.assertThat(actual.get().getGamesPlayed()).isEqualTo(10);
    }

    private PlayerStatisticApiDto createPlayerStatisticApiDto() {
        return new PlayerStatisticApiDto(
                /* get */    "players",
                /* results */1,
                /* paging */ new PlayerStatisticApiDto.Paging(1, 1),
                /* response */ List.of(
                new PlayerStatisticApiDto.PlayerResponse(
                        new PlayerStatisticApiDto.Player(
                                /* id */            247,
                                /* name */          "C. Gakpo",
                                /* firstname */     "Cody Mathès",
                                /* lastname */      "Gakpo",
                                /* age */           26,
                                /* birth */         new PlayerStatisticApiDto.Birth(
                                "1999-05-07",
                                "Eindhoven",
                                "Netherlands"
                        ),
                                /* nationality */   "Netherlands",
                                /* height */        "193 cm",
                                /* weight */        "76 kg",
                                /* injured */       false,
                                /* photo */         "https://media.api-sports.io/football/players/247.png"
                        ),
                        List.of(
                                new PlayerStatisticApiDto.Statistic(
                                        new PlayerStatisticApiDto.Team(
                                                /* id */   40,
                                                /* name */ "Liverpool",
                                                /* logo */ "https://media.api-sports.io/football/teams/40.png"
                                        ),
                                        new PlayerStatisticApiDto.League(
                                                /* id */      39,
                                                /* name */    "Premier League",
                                                /* country */ "England",
                                                /* logo */    "https://media.api-sports.io/football/leagues/39.png",
                                                /* flag */    "https://media.api-sports.io/flags/gb-eng.svg",
                                                /* season */  2024
                                        ),
                                        new PlayerStatisticApiDto.Games(
                                                /* appearences */ 31,
                                                /* lineups */     19,
                                                /* minutes */     1629,
                                                /* number */      null,
                                                /* position */    "Attacker",
                                                /* rating */      "7.206451",
                                                /* captain */     false
                                        ),
                                        new PlayerStatisticApiDto.Substitutes(12, 15, 12),
                                        new PlayerStatisticApiDto.Shots(41, 21),
                                        new PlayerStatisticApiDto.Goals(9, 0, 3, null),
                                        new PlayerStatisticApiDto.Passes(469, 33, null),
                                        new PlayerStatisticApiDto.Tackles(26, 2, 11),
                                        new PlayerStatisticApiDto.Duels(176, 91),
                                        new PlayerStatisticApiDto.Dribbles(42, 27, null),
                                        new PlayerStatisticApiDto.Fouls(23, 17),
                                        new PlayerStatisticApiDto.Cards(5, 0, 0),
                                        new PlayerStatisticApiDto.Penalty(null, null, 0, 0, null)
                                )
                        )
                )
        ));
    }

    private PlayerStat dtoToEntity(Long playerId, PlayerStatisticApiDto dto) {
        PlayerStatisticApiDto.Statistic statistic = dto.response().getFirst().statistics().getFirst();
        return PlayerStat.builder()
                .gamesPlayed(statistic.games().appearences())
                .substitutes(statistic.substitutes().in())
                .goal(statistic.goals().total())
                .assist(statistic.goals().assists())
                .pk(statistic.penalty().won())
                .rating(statistic.games().rating())
                .yellowCards(statistic.cards().yellow())
                .redCards(statistic.cards().red())
                .build();
    }
}
