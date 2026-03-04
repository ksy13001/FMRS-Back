package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.StatFreshness;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.player.PlayerStatResponse;
import com.ksy.fmrs.exception.EmptyResponseException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    @DisplayName("stat이 없으면 MISSING 응답")
    void getPlayerStatById_missing() {
        // given
        Long playerId = 1L;
        Player player = Player.builder().playerApiId(1).build();
        ReflectionTestUtils.setField(player, "id", playerId);

        // when
        when(playerRepository.findWithPlayerStatById(playerId)).thenReturn(Optional.of(player));
        when(ttlProvider.getTtl()).thenReturn(Duration.ofDays(1));

        PlayerStatResponse actual = playerStatService.getPlayerStatById(playerId);

        // then
        Assertions.assertThat(actual.getStatFreshness()).isEqualTo(StatFreshness.MISSING);
        Assertions.assertThat(actual.isNeedsStatRefresh()).isTrue();
        Assertions.assertThat(actual.getGamesPlayed()).isNull();
        verifyNoInteractions(apiFootballClient, playerStatRepository, playerStatMapper);
    }

    @Test
    @DisplayName("유효한 stat이면 FRESH 응답")
    void getPlayerStatById_fresh() {
        // given
        Long playerId = 1L;
        Player player = Player.builder().playerApiId(1).build();
        PlayerStat playerStat = PlayerStat.builder().gamesPlayed(10).build();
        ReflectionTestUtils.setField(player, "id", playerId);
        ReflectionTestUtils.setField(playerStat, "modifiedDate", Instant.EPOCH.minus(23L, ChronoUnit.HOURS));
        player.updatePlayerStat(playerStat);

        // when
        when(playerRepository.findWithPlayerStatById(playerId)).thenReturn(Optional.of(player));
        when(ttlProvider.getTtl()).thenReturn(Duration.ofDays(1));

        PlayerStatResponse actual = playerStatService.getPlayerStatById(playerId);

        // then
        Assertions.assertThat(actual.getStatFreshness()).isEqualTo(StatFreshness.FRESH);
        Assertions.assertThat(actual.isNeedsStatRefresh()).isFalse();
        Assertions.assertThat(actual.getGamesPlayed()).isEqualTo(10);
        verifyNoInteractions(apiFootballClient, playerStatRepository, playerStatMapper);
    }

    @Test
    @DisplayName("만료된 stat이면 EXPIRED 응답")
    void getPlayerStatById_expired() {
        // given
        Long playerId = 1L;
        Player player = Player.builder().playerApiId(1).build();
        PlayerStat playerStat = PlayerStat.builder().gamesPlayed(10).build();
        ReflectionTestUtils.setField(player, "id", playerId);
        ReflectionTestUtils.setField(playerStat, "modifiedDate", Instant.EPOCH.minus(25L, ChronoUnit.HOURS));
        player.updatePlayerStat(playerStat);

        // when
        when(playerRepository.findWithPlayerStatById(playerId)).thenReturn(Optional.of(player));
        when(ttlProvider.getTtl()).thenReturn(Duration.ofDays(1));

        PlayerStatResponse actual = playerStatService.getPlayerStatById(playerId);

        // then
        Assertions.assertThat(actual.getStatFreshness()).isEqualTo(StatFreshness.EXPIRED);
        Assertions.assertThat(actual.isNeedsStatRefresh()).isTrue();
        Assertions.assertThat(actual.getGamesPlayed()).isEqualTo(10);
        verifyNoInteractions(apiFootballClient, playerStatRepository, playerStatMapper);
    }

    @Test
    @DisplayName("refresh 호출 시 외부 API 기반으로 stat 저장 후 FRESH 반환")
    void savePlayerStat_success() {
        // given
        Long playerId = 1L;
        Player player = Player.builder().playerApiId(1).build();
        Team team = Team.builder().name("Team").teamApiId(1).build();
        League league = League.builder().name("League").currentSeason(2024).leagueApiId(1).build();
        ApiFootballPlayersStatistics apiDto = createPlayerStatisticApiDto();

        ReflectionTestUtils.setField(player, "id", playerId);
        team.updateLeague(league);
        player.updateTeam(team);

        PlayerStat refreshed = PlayerStat.builder().gamesPlayed(12).build();

        // when
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(apiFootballClient.requestPlayerStatistics(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(apiDto);
        when(playerStatMapper.toEntity(apiDto)).thenReturn(refreshed);

        PlayerStatResponse actual = playerStatService.savePlayerStat(playerId);

        // then
        verify(playerStatRepository, times(1)).save(refreshed);
        verify(apiFootballClient).requestPlayerStatistics(
                league.getLeagueApiId(),
                team.getTeamApiId(),
                player.getPlayerApiId(),
                league.getCurrentSeason()
        );
        Assertions.assertThat(actual.getStatFreshness()).isEqualTo(StatFreshness.FRESH);
        Assertions.assertThat(actual.isNeedsStatRefresh()).isFalse();
        Assertions.assertThat(actual.getGamesPlayed()).isEqualTo(12);
    }

    @Test
    @DisplayName("refresh 중 외부 응답이 비어 있으면 EmptyResponseException 전파")
    void savePlayerStat_emptyResponse() {
        // given
        Long playerId = 1L;
        int currentSeason = 2025;

        Player player = Player.builder().name("p1").build();
        Team team = Team.builder().build();
        League league = League.builder().currentSeason(currentSeason).build();
        player.updateTeam(team);
        team.updateLeague(league);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(apiFootballClient.requestPlayerStatistics(null, null, null, currentSeason)).thenReturn(null);
        when(playerStatMapper.toEntity(any())).thenThrow(new EmptyResponseException("PlayerStat response is empty"));

        // when/then
        Assertions.assertThatThrownBy(() -> playerStatService.savePlayerStat(playerId))
                .isInstanceOf(EmptyResponseException.class);
        verify(playerStatRepository, never()).save(any());
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
                                                LocalDate.of(1999, 5, 7),
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
}
