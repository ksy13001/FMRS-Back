package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.PlayerStat;
import com.ksy.fmrs.domain.enums.UrlEnum;
import com.ksy.fmrs.dto.*;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.PlayerStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FootballApiService {

    @Value("${api-football.key}")
    private String apiFootballKey;
    @Value("${api-football.host}")
    private String apiFootballHost;

    private final PlayerStatRepository playerStatRepository;
    private final PlayerRepository playerRepository;

    /**
     * 1. playerDetailDto 에서 playerName, teamName 가져옴
     * 2. teamName 을 통해 api-football 에서 teamApiId 가져옴
     * 3. playerName + teamApiId 를 통해 api-football 에서 playerApi + playerRealStat 가져옴
     */
    @Transactional
    public PlayerStatDto getPlayerRealStat(Long playerId, String playerName, String teamName) {

        return getOptionalPlayerStatById(playerId)
                .map(PlayerStatDto::new)
                .orElseGet(() -> {
                    String url = UrlEnum.buildPlayerStatUrl(splitPlayerName(playerName), getTeamApiIdByTeamName(teamName));
                    PlayerStatisticsApiResponseDto response = getApiResponse(url, PlayerStatisticsApiResponseDto.class);
                    PlayerStatDto playerStatDto = convertStatisticsToPlayerStatDto(response);
                    updatePlayerImage(playerId, playerStatDto.getImageUrl());
                    savePlayerStat(convertPlayerStatDtoToPlayerStat(playerId, playerStatDto));
                    return playerStatDto;
                });
    }

    public LeagueDetailsDto getLeagueDetails(Integer leagueId) {
        String url = UrlEnum.buildStandingUrl(leagueId);
        StandingsAPIResponseDto response =  getApiResponse(url, StandingsAPIResponseDto.class);
        return getValidatedLeagueDetails(response,  leagueId);
    }

    public List<PlayerSimpleDto> getLeagueTopScorers(Integer leagueId) {
        String url = UrlEnum.buildTopScorersUrl(leagueId);
        LeagueApiTopPlayerResponseDto response = getApiResponse(url, LeagueApiTopPlayerResponseDto.class);
        return convertToPlayerSimpleDtoList(response);
    }

    public List<PlayerSimpleDto> getLeagueTopAssists(Integer leagueId) {
        String url =  UrlEnum.buildTopAssistsUrl(leagueId);
        LeagueApiTopPlayerResponseDto response = getApiResponse(url, LeagueApiTopPlayerResponseDto.class);
        return convertToPlayerSimpleDtoList(response);
    }


    private String splitPlayerName(String fullName) {
        String[] tokens = fullName.trim().split("\\s+");
        if (tokens.length > 1) {
            return tokens[1];
        }
        return tokens[tokens.length - 1];
    }


    private String truncateToTwoDecimalsRanging(String r) {
        if (r == null || r.isEmpty()) {
            return "0";
        }
        double rating = Double.parseDouble(r);
        return String.format("%.2f", rating);
    }

    private Integer getTeamApiIdByTeamName(String teamName) {
        String url = UrlEnum.buildTeamUrl(teamName);

        TeamApiResponseDto teamApiResponseDto = getApiResponse(url, TeamApiResponseDto.class);
        return teamApiResponseDto
                .getResponse()
                .getFirst()
                .getTeam()
                .getId();
    }

    // API 호출 공통 로직
    private <T> T  getApiResponse(String url, Class<T> responseType) {
        ResponseEntity<T> responseEntity = createAPIFootballRestClient()
                .get()
                .uri(url)
                .retrieve()
                .toEntity(responseType);

        return validateResponse(responseEntity, url);
    }

    private <T> T validateResponse(ResponseEntity<T> responseEntity, String url) {
        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new IllegalArgumentException("API 호출 결과가 없습니다. URL: " + url);
        }
        return  responseEntity.getBody();
    }

    private LeagueDetailsDto getValidatedLeagueDetails(StandingsAPIResponseDto response, Integer leagueId) {
        if(response.getResults()<=0){
            throw new IllegalArgumentException("not exist league standing id:"+leagueId);
        }
        return convertStandingToLeagueDetailsDto(response);
    }

    private RestClient createAPIFootballRestClient() {
        return RestClient.builder()
                .defaultHeader("X-RapidAPI-Key", apiFootballKey)
                .defaultHeader("X-RapidAPI-Host", apiFootballHost)
                .build();
    }


    private void updatePlayerImage(Long playerId, String imageUrl) {
        Player player = findPlayerById(playerId);
        player.updateImageUrl(imageUrl);
    }

    private void savePlayerStat(PlayerStat playerStat) {
        playerStatRepository.save(playerStat);
    }

    private Optional<PlayerStat> getOptionalPlayerStatById(Long playerStatId) {
        return playerStatRepository.findById(playerStatId);
    }

    private Player findPlayerById(Long playerId) {
        return playerRepository.findById(playerId).orElseThrow(
                () -> new IllegalArgumentException("Player with id " + playerId + " not found")
        );
    }


    private PlayerStat convertPlayerStatDtoToPlayerStat(Long playerId, PlayerStatDto playerStatDto) {
        return PlayerStat.builder()
                .playerId(playerId)
                .apiFootballId(playerStatDto.getApiFootballId())
                .gamesPlayed(playerStatDto.getGamesPlayed())
                .goal(playerStatDto.getGoal())
                .assist(playerStatDto.getAssist())
                .pk(playerStatDto.getPk())
                .rating(playerStatDto.getRating())
                .imageUrl(playerStatDto.getImageUrl())
                .build();
    }

    // 출장경기수, 골, 어시스트, 평점, 선수 이미지
    private PlayerStatDto convertStatisticsToPlayerStatDto(PlayerStatisticsApiResponseDto response) {
        PlayerStatisticsApiResponseDto.StatisticDto stat = response.getResponse().getFirst().getStatistics().getFirst();
        PlayerStatDto playerStatDto = new PlayerStatDto();
        playerStatDto.setApiFootballId(response.getResponse().getFirst().getPlayer().getId());
        playerStatDto.setGamesPlayed(stat.getGames().getAppearences());
        playerStatDto.setGoal(stat.getGoals().getTotal());
        playerStatDto.setAssist(stat.getGoals().getAssists());
        playerStatDto.setPk(stat.getPenalty().getScored());
        playerStatDto.setRating(truncateToTwoDecimalsRanging(stat.getGames().getRating()));
        playerStatDto.setImageUrl(response.getResponse().getFirst().getPlayer().getPhoto());
        return playerStatDto;
    }

    private LeagueDetailsDto convertStandingToLeagueDetailsDto(StandingsAPIResponseDto response) {
        StandingsAPIResponseDto.League league = response.getResponse().getFirst().getLeague();
        List<TeamStandingDto> teamStandingDtos = league.getStandings().getFirst().stream()
                .map(this::convertStandingToTeamStandingDto)
                .toList();
        return LeagueDetailsDto.builder()
                .name(league.getName())
                .country(league.getCountry())
                .logoUrl(league.getLogo())
                .currentSeason(league.getSeason())
                .standings(teamStandingDtos)
                .build();
    }

    private TeamStandingDto convertStandingToTeamStandingDto(StandingsAPIResponseDto.Standing standing) {
        return TeamStandingDto.builder()
                .rank(standing.getRank())
                .teamId(standing.getTeam().getId())
                .teamName(standing.getTeam().getName())
                .teamLogo(standing.getTeam().getLogo())
                .played(standing.getAll().getPlayed())
                .won(standing.getAll().getWin())
                .drawn(standing.getAll().getDraw())
                .lost(standing.getAll().getLose())
                .goalsFor(standing.getAll().getGoals().getGoalsFor())
                .goalsAgainst(standing.getAll().getGoals().getAgainst())
                .points(standing.getPoints())
                .goalsDifference(standing.getGoalsDiff())
                .description(standing.getDescription())
                .form(standing.getForm())
                .build();
    }

    private List<PlayerSimpleDto> convertToPlayerSimpleDtoList(LeagueApiTopPlayerResponseDto  response) {
        return response.getResponse()
                .stream()
                .map((playerWrapper)-> PlayerSimpleDto.builder()
                        .name(playerWrapper.getPlayer().getName())
                        .teamName(playerWrapper.getStatistics().getFirst().getTeam().getName())
                        .age(playerWrapper.getPlayer().getAge())
                        .goal(playerWrapper.getStatistics().getFirst().getGoals().getTotal())
                        .assist(playerWrapper.getStatistics().getFirst().getGoals().getAssists())
                        .rating(playerWrapper.getStatistics().getFirst().getGames().getRating())
                        .imageUrl(playerWrapper.getPlayer().getPhoto())
                        .build()).toList();
    }
}