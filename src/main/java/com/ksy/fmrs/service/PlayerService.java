package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.*;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlayerService {
    @Value("${api-football.key}")
    private String apiFootballKey;
    @Value("${api-football.host}")
    private String apiFootballHost;

    private static final String TEAM_URL = "https://v3.football.api-sports.io/teams?";
    private static final String PLAYER_STAT_URL = "https://v3.football.api-sports.io/players?";
    private static final String NAME = "name=";
    private static final String TEAM_ID = "team=";
    private static final String AND = "&";
    private final RestTemplate restTemplate;
    private final PlayerRepository playerRepository;

    public PlayerDetailsResponseDto getPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return convertToPlayerDetailsResponseDto(player);
    }

    private PlayerDetailsResponseDto convertToPlayerDetailsResponseDto(Player player) {
        String teamName = Optional.ofNullable(player.getTeam())
                .map(Team::getName)
                .orElse(null);
        return new PlayerDetailsResponseDto(player, teamName);
    }

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

    public Integer getTeamApiIdByTeamName(String teamName){
        String url = TEAM_URL+NAME+teamName;

        ResponseEntity<TeamApiResponseDto> teamApiResponseDto = createAPIFootballRestClient()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(TeamApiResponseDto.class);

       return teamApiResponseDto.getBody().getResponse().get(0).getTeam().getId();
    }

    public RestClient createAPIFootballRestClient(){
        return RestClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .build();
    }

    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertToPlayerDetailsResponseDto)
                .toList());
    }

    public SearchPlayerResponseDto getPlayersByMarketValueDesc() {
        return new SearchPlayerResponseDto(playerRepository.findAllByOrderByMarketValueDesc()
                .stream()
                .map(this::convertToPlayerDetailsResponseDto)
                .toList());
    }

    public SearchPlayerResponseDto searchPlayerByName(String name) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByName(name)
                .stream()
                .map(this::convertToPlayerDetailsResponseDto)
                .toList());
    }

    public SearchPlayerResponseDto searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(this::convertToPlayerDetailsResponseDto)
                .toList());
    }
}
