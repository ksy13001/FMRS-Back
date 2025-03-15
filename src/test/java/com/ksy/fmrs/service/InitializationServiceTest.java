package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.util.StringUtils;
import io.micrometer.core.instrument.util.TimeUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class InitializationServiceTest {

    @InjectMocks
    private InitializationService initializationService;
    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private FootballApiService footballApiService;
    @Mock
    private PlayerRepository playerRepository;


    @Test
    @DisplayName("리그, 팀 초기 생성 테스트")
    void 메서드명(){
        // given
        LeagueDetailsRequestDto leagueDetailsRequestDto = LeagueDetailsRequestDto.builder()
                .leagueApiId(1)
                .leagueName("League1")
                .leagueType(LeagueType.LEAGUE.getValue())
                .logoImageUrl("league1")
                .nationName("Nation1")
                .nationImageUrl("nation1")
                .currentSeason(2024)
                .Standing(true)
                .build();

        // when

        // then
    }


    @Test
    @DisplayName("선수 player_api_id 매핑 테스트")
    void haalandMapping(){
        String apiFirstName = "Erling";
        String apiLastName = "Braut Haaland";
        String apiBirth = "2000-07-21";

        String dbFirstName = "Erling";
        String dbLastName = "Haaland";
        LocalDate dbBirth = LocalDate.of(2000,  7, 21);

        Assertions.assertThat(StringUtils.getFirstName(apiFirstName)).isEqualTo(dbFirstName);
        Assertions.assertThat(StringUtils.getLastName(apiLastName)).isEqualTo(dbLastName);
        Assertions.assertThat(StringUtils.parseStringToLocalDate(apiBirth)).isEqualTo(dbBirth);
    }
}