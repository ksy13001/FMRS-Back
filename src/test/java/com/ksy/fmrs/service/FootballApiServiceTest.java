package com.ksy.fmrs.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;

import java.util.Optional;

import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

@ExtendWith(MockitoExtension.class)
public class FootballApiServiceTest {

    @Spy
    @InjectMocks
    private FootballApiService footballApiService;

    @Mock
    private PlayerStatRepository playerStatRepository;

    @Mock
    private PlayerRepository playerRepository;

    // RestClient 체인을 위한 목 객체들
    @Mock
    private RestClient mockRestClientTeam;
    @Mock
    private RestClient mockRestClientPlayer;
    @Mock
    private RequestHeadersUriSpec<?> mockTeamUriSpec;
    @Mock
    private RequestHeadersSpec<?> mockTeamHeadersSpec;
    @Mock
    private ResponseSpec mockTeamResponseSpec;
    @Mock
    private RequestHeadersUriSpec<?> mockPlayerUriSpec;
    @Mock
    private RequestHeadersSpec<?> mockPlayerHeadersSpec;
    @Mock
    private ResponseSpec mockPlayerResponseSpec;

    /**
     * 시나리오 1: DB에 통계가 없는 경우
     * - 외부 API 호출 후 팀 API ID 및 선수 통계 정보를 받아와 DTO 변환,
     *   Player 이미지 업데이트, DB 저장이 수행되는지 검증.
     */
//    @Test
//    public void testGetPlayerRealStat_whenNotInDB() {
//        // given
//        Long playerId = 1L;
//        String playerName = "Neymar";
//        String teamName = "Paris Saint Germain";
//
//        // DB에 기존 통계가 없으므로 Optional.empty() 반환
//        when(playerStatRepository.findById(playerId)).thenReturn(Optional.empty());
//
//        // dummy Player 생성 (이미지 업데이트 확인)
//        Player dummyPlayer = Player.builder().build();
//        ReflectionTestUtils.setField(dummyPlayer, "id", playerId
//        );
//        when(playerRepository.findById(playerId)).thenReturn(Optional.of(dummyPlayer));
//
//        // 팀 API 응답 구성: team API ID 100 반환
//        TeamApiResponseDto.TeamDto teamDto = new TeamApiResponseDto.TeamDto();
//        teamDto.setId(100);
//        teamDto.setName(teamName);
//        TeamApiResponseDto.TeamWrapperDto teamWrapper = new TeamApiResponseDto.TeamWrapperDto();
//        teamWrapper.setTeam(teamDto);
//        TeamApiResponseDto teamApiResponse = new TeamApiResponseDto();
//        teamApiResponse.setResponse(List.of(teamWrapper));
//        ResponseEntity<TeamApiResponseDto> teamResponseEntity = ResponseEntity.ok(teamApiResponse);
//
//        // 선수 통계 API 응답 구성
//        PlayerStatisticsApiResponseDto.PlayerDto playerDto = new PlayerStatisticsApiResponseDto.PlayerDto();
//        playerDto.setId(276);
//        playerDto.setPhoto("dummyPhotoUrl");
//        // dummy Statistic 정보: 경기수 15, 골 13, 어시스트 6, 페널티 득점 4, 평점 "8.053333"
//        PlayerStatisticsApiResponseDto.StatisticDto.GamesDto gamesDto = new PlayerStatisticsApiResponseDto.StatisticDto.GamesDto();
//        gamesDto.setAppearences(15);
//        gamesDto.setRating("8.053333");
//        PlayerStatisticsApiResponseDto.StatisticDto.GoalsDto goalsDto = new PlayerStatisticsApiResponseDto.StatisticDto.GoalsDto();
//        goalsDto.setTotal(13);
//        goalsDto.setAssists(6);
//        PlayerStatisticsApiResponseDto.StatisticDto.PenaltyDto penaltyDto = new PlayerStatisticsApiResponseDto.StatisticDto.PenaltyDto();
//        penaltyDto.setScored(4);
//        PlayerStatisticsApiResponseDto.StatisticDto statisticDto = new PlayerStatisticsApiResponseDto.StatisticDto();
//        statisticDto.setGames(gamesDto);
//        statisticDto.setGoals(goalsDto);
//        statisticDto.setPenalty(penaltyDto);
//        // dummy PlayerWrapper 구성
//        PlayerStatisticsApiResponseDto.PlayerWrapperDto playerWrapper = new PlayerStatisticsApiResponseDto.PlayerWrapperDto();
//        playerWrapper.setPlayer(playerDto);
//        playerWrapper.setStatistics(List.of(statisticDto));
//        PlayerStatisticsApiResponseDto playerStatsResponse = new PlayerStatisticsApiResponseDto();
//        playerStatsResponse.setResponse(List.of(playerWrapper));
//        ResponseEntity<PlayerStatisticsApiResponseDto> playerStatsResponseEntity = ResponseEntity.ok(playerStatsResponse);
//
//        // RestClient 체인 모의: createAPIFootballRestClient()가 두 번 호출됨 (팀 API, 선수 통계 API)
//        // 팀 API 호출 체인
//        when(mockRestClientTeam.get()).thenReturn(mockTeamUriSpec);
//        when(mockTeamUriSpec.uri(anyString())).thenReturn(mockTeamHeadersSpec);
//        when(mockTeamHeadersSpec.retrieve()).thenReturn(mockTeamResponseSpec);
//        when(mockTeamResponseSpec.toEntity(TeamApiResponseDto.class)).thenReturn(teamResponseEntity);
//        // 선수 통계 API 호출 체인
//        when(mockRestClientPlayer.get()).thenReturn(mockPlayerUriSpec);
//        when(mockPlayerUriSpec.uri(anyString())).thenReturn(mockPlayerHeadersSpec);
//        when(mockPlayerHeadersSpec.retrieve()).thenReturn(mockPlayerResponseSpec);
//        when(mockPlayerResponseSpec.toEntity(PlayerStatisticsApiResponseDto.class))
//                .thenReturn(playerStatsResponseEntity);
//
//        // FootballApiService의 createAPIFootballRestClient()를 스파이에서 stub 처리
//        doReturn(mockRestClientTeam, mockRestClientPlayer)
//                .when(footballApiService).createAPIFootballRestClient();
//
//        // when
//        PlayerStatDto result = footballApiService.getPlayerRealStat(playerId, playerName, teamName);
//
//        // then
//        assertNotNull(result);
//        assertEquals(15, result.getGamesPlayed());
//        assertEquals(13, result.getGoal());
//        assertEquals(6, result.getAssist());
//        assertEquals(4, result.getPk());
//        assertEquals("8.053333", result.getRating());
//        assertEquals("dummyPhotoUrl", result.getImageUrl());
//
//        // 외부 API 호출 후 새 통계가 DB에 저장되었음을 검증
//        verify(playerStatRepository).save(any(PlayerStat.class));
//        // Player 이미지 업데이트를 위해 playerRepository.findById()가 호출되었음을 검증
//        verify(playerRepository).findById(playerId);
//    }

//    /**
//     * 시나리오 2: DB에 기존 선수 통계가 존재하는 경우
//     * - 외부 API 호출 없이 DB의 정보를 DTO로 변환하여 반환함.
//     */
//    @Test
//    public void testSavePlayerRealStat_whenExistsInDB() {
//        // given
//        Long playerId = 1L;
//        String playerName = "Neymar";
//        String teamName = "Paris Saint Germain";
//
//        // 기존 통계 객체 생성
//        PlayerStat existingStat = PlayerStat.builder()
//                .playerId(playerId)
//                .gamesPlayed(10)
//                .goal(5)
//                .assist(3)
//                .pk(2)
//                .rating("7.0")
//                .build();
//        when(playerStatRepository.findById(playerId)).thenReturn(Optional.of(existingStat));
//
//        // when
//        PlayerStatDto result = footballApiService.savePlayerRealStat(playerId, 2024);
//
//        // then
//        assertNotNull(result);
//        assertEquals(10, result.getGamesPlayed());
//        assertEquals(5, result.getGoal());
//        assertEquals(3, result.getAssist());
//        assertEquals(2, result.getPk());
//        assertEquals("7.0", result.getRating());
//
//        // 외부 API 호출 없이 바로 반환되었으므로 save() 호출되지 않음
//        verify(playerStatRepository, never()).save(any(PlayerStat.class));
//    }

    @Test
    void 메서드명() throws Exception{
        // given
        String  playerName = "Neymar";
        Integer teamId = 276;
        // when
        Assertions.assertThat("Neymar276").isEqualTo(playerName+teamId);
        // then
    }
}
