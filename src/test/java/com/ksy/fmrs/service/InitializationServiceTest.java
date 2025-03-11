package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.LeagueType;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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



}