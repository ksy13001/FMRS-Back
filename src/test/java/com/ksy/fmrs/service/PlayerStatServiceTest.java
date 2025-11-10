package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.mapper.PlayerStatMapper;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import com.ksy.fmrs.util.PlayerStatTtlProvider;
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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
    private ApiFootballClient apiFootballClient;
    @Mock
    private PlayerStatMapper playerStatMapper;
    @Mock
    private PlayerStatTtlProvider ttlProvider;
    @Spy
    private TimeProvider timeProvider =
            new TestTimeProvider(
                    LocalDateTime.of(2000, 8, 14, 0, 0),
                    LocalDate.of(2000, 8, 14),
                    new Date(),
                    Instant.EPOCH
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
        ApiFootballPlayersStatistics playerStatisticApiDto = createPlayerStatisticApiDto();
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
        when(apiFootballClient.requestPlayerStatistics(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(playerStatisticApiDto);
        when(playerStatMapper.toEntity(any())).thenReturn(playerStat);

        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(player.getId());
        // then
        verify(playerStatRepository, times(1)).save(playerStat);
        verify(apiFootballClient).requestPlayerStatistics(
                league.getLeagueApiId(),
                team.getTeamApiId(),
                player.getPlayerApiId(),
                league.getCurrentSeason()
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
        ApiFootballPlayersStatistics playerStatisticApiDto = createPlayerStatisticApiDto();

        PlayerStat existingStat = PlayerStat.builder()
                .gamesPlayed(10)
                .build();
        ReflectionTestUtils.setField(player, "id", playerId);
        // 하루 된 스탯
        ReflectionTestUtils.setField(existingStat, "modifiedDate",
                Instant.EPOCH.minus(24L, ChronoUnit.HOURS));
        PlayerStat refreshStat = PlayerStat.builder()
                .gamesPlayed(12)
                .build();

        player.updatePlayerStat(existingStat);
        team.updateLeague(league);
        player.updateTeam(team);

        // when
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(apiFootballClient.requestPlayerStatistics(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(playerStatisticApiDto);
        when(playerStatMapper.toEntity(any())).thenReturn(refreshStat);
        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(playerId);
        // then
        verify(playerRepository, times(1)).findById(playerId);
        verify(playerStatRepository, times(1)).save(refreshStat);
        verify(apiFootballClient).requestPlayerStatistics(
                league.getLeagueApiId(),
                team.getTeamApiId(),
                player.getPlayerApiId(),
                league.getCurrentSeason()
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
        Duration ttl = Duration.ofDays(1);
        ReflectionTestUtils.setField(player, "id", playerId);
        ReflectionTestUtils.setField(existingStat, "modifiedDate",
                Instant.EPOCH.minus(23L, ChronoUnit.HOURS));

        player.updatePlayerStat(existingStat);
        team.updateLeague(league);
        player.updateTeam(team);

        // when
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(ttlProvider.getTtl()).thenReturn(ttl);
        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(playerId);
        // then
        verify(playerRepository, times(1)).findById(playerId);
        verifyNoInteractions(apiFootballClient);

        Assertions.assertThat(actual.get().getGamesPlayed()).isEqualTo(10);
    }

    @Test
    @DisplayName("player에 팀이 존재하고 stat 존재하지 않아 playerStat값 요청했는데 null 값이 반환된 경우, Empty 반환")
    void save_player_stat_with_empty_response(){
        // given
        int currentSeason = 2025;
        Long playerId = 1L;
        Player player = Player.builder().name("p1").build();
        Team team = Team.builder().build();
        player.updateTeam(team);
        League league = League.builder().currentSeason(currentSeason).build();
        team.updateLeague(league);
        PlayerStat playerStat = PlayerStat.builder().build();

        // when
        when(playerRepository.findById(playerId))
                .thenReturn(Optional.of(player));
        when(apiFootballClient.requestPlayerStatistics(null, null, null, currentSeason))
                .thenReturn(null);
        when(playerStatMapper.toEntity(any())).thenReturn(null);

        Optional<PlayerStatDto> actual = playerStatService.saveAndGetPlayerStat(playerId);
        // then
        verify(playerRepository, never()).save(any());
        Assertions.assertThat(actual.isPresent()).isFalse();
    }

    private ApiFootballPlayersStatistics createPlayerStatisticApiDto() {
        return new ApiFootballPlayersStatistics(
                "players",
                new ApiFootballPlayersStatistics.ParametersDto("247", "2024"),
                List.of(),
                1,
                new ApiFootballPlayersStatistics.PagingDto(1, 1),
                List.of(
                        new ApiFootballPlayersStatistics.PlayerWrapperDto(
                                new ApiFootballPlayersStatistics.PlayerDto(
                                        247,
                                        "C. Gakpo",
                                        "Cody Mathès",
                                        "Gakpo",
                                        26,
                                        new ApiFootballPlayersStatistics.BirthDto(
                                                java.time.LocalDate.of(1999, 5, 7),
                                                "Eindhoven",
                                                "Netherlands"
                                        ),
                                        "Netherlands",
                                        "193 cm",
                                        "76 kg",
                                        false,
                                        "https://media.api-sports.io/football/players/247.png"
                                ),
                                List.of(
                                        new ApiFootballPlayersStatistics.StatisticDto(
                                                new ApiFootballPlayersStatistics.StatisticDto.TeamDto(
                                                        40,
                                                        "Liverpool",
                                                        "https://media.api-sports.io/football/teams/40.png"
                                                ),
                                                new ApiFootballPlayersStatistics.StatisticDto.LeagueDto(
                                                        39,
                                                        "Premier League",
                                                        "England",
                                                        "https://media.api-sports.io/football/leagues/39.png",
                                                        "https://media.api-sports.io/flags/gb-eng.svg",
                                                        "2024"
                                                ),
                                                new ApiFootballPlayersStatistics.StatisticDto.GamesDto(
                                                        31,
                                                        19,
                                                        1629,
                                                        null,
                                                        "Attacker",
                                                        "7.206451",
                                                        false
                                                ),
                                                new ApiFootballPlayersStatistics.StatisticDto.SubstitutesDto(12, 15, 12),
                                                new ApiFootballPlayersStatistics.StatisticDto.ShotsDto(41, 21),
                                                new ApiFootballPlayersStatistics.StatisticDto.GoalsDto(9, 0, 3, null),
                                                new ApiFootballPlayersStatistics.StatisticDto.PassesDto(469, 33, null),
                                                new ApiFootballPlayersStatistics.StatisticDto.TacklesDto(26, 2, 11),
                                                new ApiFootballPlayersStatistics.StatisticDto.DuelsDto(176, 91),
                                                new ApiFootballPlayersStatistics.StatisticDto.DribblesDto(42, 27, null),
                                                new ApiFootballPlayersStatistics.StatisticDto.FoulsDto(23, 17),
                                                new ApiFootballPlayersStatistics.StatisticDto.CardsDto(5, 0, 0),
                                                new ApiFootballPlayersStatistics.StatisticDto.PenaltyDto(null, null, 0, 0, null)
                                        )
                                )
                        )
                )
        );
    }

    private PlayerStat dtoToEntity(Long playerId, ApiFootballPlayersStatistics dto) {
        ApiFootballPlayersStatistics.StatisticDto statistic = dto.response().getFirst().statistics().getFirst();
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
