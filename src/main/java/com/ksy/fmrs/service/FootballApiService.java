package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.PlayerRealFootballStatDto;
import com.ksy.fmrs.dto.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.TeamApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class FootballApiService {

    @Value("${api-football.key}")
    private String apiFootballKey;
    @Value("${api-football.host}")
    private String apiFootballHost;

    private static final String TEAM_URL = "https://v3.football.api-sports.io/teams?";
    private static final String PLAYER_STAT_URL = "https://v3.football.api-sports.io/players?";
    private static final String NAME = "name=";
    private static final String TEAM_ID = "team=";
    private static final String AND = "&";

    /**
     * 1. playerDetailDto 에서 playerName, teamName 가져옴
     * 2. teamName 을 통해 api-football 에서 teamApiId 가져옴
     * 3. playerName + teamApiId 를 통해 api-football 에서 playerApi + playerRealStat 가져옴
     * */
    public PlayerRealFootballStatDto getPlayerRealStat(String playerName, String teamName) {
        String url = PLAYER_STAT_URL + NAME + playerName + AND + TEAM_ID + getTeamApiIdByTeamName(teamName);
        ResponseEntity<PlayerStatisticsApiResponseDto> response = createAPIFootballRestClient()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(PlayerStatisticsApiResponseDto.class);

        return getPlayerRealFootballStatByStatistics(response.getBody());
    }

    // 출장경기수, 골, 어시스트, 평점
    private PlayerRealFootballStatDto getPlayerRealFootballStatByStatistics(PlayerStatisticsApiResponseDto response) {
        PlayerStatisticsApiResponseDto.StatisticDto stat = response.getResponse().get(0).getStatistics().get(0);
        PlayerRealFootballStatDto playerRealStatDto = new PlayerRealFootballStatDto();
        playerRealStatDto.setGamesPlayed(stat.getGames().getAppearences());
        playerRealStatDto.setGoal(stat.getGoals().getTotal());
        playerRealStatDto.setAssist(stat.getGoals().getAssists());
        playerRealStatDto.setPk(stat.getPenalty().getScored());
        playerRealStatDto.setRating(stat.getGames().getRating());
        return playerRealStatDto;
    }

    private Integer getTeamApiIdByTeamName(String teamName){
        String url = TEAM_URL+NAME+teamName;

        ResponseEntity<TeamApiResponseDto> teamApiResponseDto = createAPIFootballRestClient()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(TeamApiResponseDto.class);

        return teamApiResponseDto.getBody().getResponse().get(0).getTeam().getId();
    }

    private RestClient createAPIFootballRestClient(){
        return RestClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .build();
    }
}
