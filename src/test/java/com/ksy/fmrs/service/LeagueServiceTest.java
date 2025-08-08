package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.dto.league.LeagueAPIDetailsResponseDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @InjectMocks
    private LeagueService leagueService;
    @Mock
    private FootballApiService footballApiService;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private LeagueRepository leagueRepository;

    @Test
    @DisplayName("apiFootball 에서 리그 정보 가져 오기")
    void success_findLeagueApiInfo() {
        // given
        Integer leagueApiId = 1234;
        LeagueAPIDetailsResponseDto dto = LeagueAPIDetailsResponseDto
                .builder().build();
        given(footballApiService.getLeagueInfo(leagueApiId))
                .willReturn(Mono.just(Optional.of(dto)));

        // when
        Optional<LeagueAPIDetailsResponseDto> actual = leagueService.findLeagueApiInfo(leagueApiId);

        // then
        Assertions.assertThat(actual.get()).isEqualTo(dto);
    }

    @Test
    @DisplayName("리그 api 정보와 api Id 로 리그 시즌 정보 업데이트")
    void success_refreshLeagueSeason() {
        // given
        Integer leagueApiId = 1234;
        League league = League.builder().leagueApiId(leagueApiId).build();
        LocalDate start = LocalDate.of(2024, 8, 14);
        LocalDate end = LocalDate.of(2025, 8, 21);
        Integer currentSeason = 2024;

        LeagueAPIDetailsResponseDto dto = LeagueAPIDetailsResponseDto.builder()
                .startDate(start)
                .endDate(end)
                .currentSeason(currentSeason)
                .build();

        given(leagueRepository.findLeagueByLeagueApiId(leagueApiId))
                .willReturn(Optional.of(league));
        // when
        leagueService.refreshLeagueSeason(leagueApiId, dto);

        // then
        Assertions.assertThat(league.getStartDate()).isEqualTo(start);
        Assertions.assertThat(league.getEndDate()).isEqualTo(end);
        Assertions.assertThat(league.getCurrentSeason()).isEqualTo(currentSeason);
    }
}