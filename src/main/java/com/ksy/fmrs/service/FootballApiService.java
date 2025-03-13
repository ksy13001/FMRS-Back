package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.domain.enums.UrlEnum;
import com.ksy.fmrs.dto.apiFootball.*;
import com.ksy.fmrs.dto.league.LeagueApiResponseDto;
import com.ksy.fmrs.dto.league.LeagueDetailsRequestDto;
import com.ksy.fmrs.dto.league.LeagueStandingDto;
import com.ksy.fmrs.dto.player.PlayerSimpleDto;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.dto.player.SquadPlayerDto;
import com.ksy.fmrs.dto.team.TeamStatisticsDto;
import com.ksy.fmrs.dto.team.TeamStandingDto;
import com.ksy.fmrs.repository.LeagueRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.PlayerStatRepository;
import com.ksy.fmrs.service.apiClient.RestClientService;
import com.ksy.fmrs.service.apiClient.WebClientService;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FootballApiService {

    private final LeagueRepository leagueRepository;
    private final PlayerStatRepository playerStatRepository;
    private final PlayerRepository playerRepository;
    private final RestClientService restClientService;
    private final WebClientService  webClientService;

    /**
     * 1. playerDetailDto 에서 playerName, teamName 가져옴
     * 2. teamName 을 통해 api-football 에서 teamApiId 가져옴
     * 3. playerName + teamApiId 를 통해 api-football 에서 playerApi + playerRealStat 가져옴
     */
    @Transactional
    public PlayerStatDto savePlayerRealStat(Long playerId, Integer playerApiId) {
        Player player =  findPlayerById(playerId);
        return getOptionalPlayerStatById(playerId)
                .map(PlayerStatDto::new)
                .orElseGet(() -> {
                    String url = UrlEnum.buildPlayerStatUrl(playerApiId, player.getTeam().getLeague().getCurrentSeason());
                    PlayerStatisticsApiResponseDto response = restClientService.getApiResponse(url, PlayerStatisticsApiResponseDto.class);
                    PlayerStatDto playerStatDto = convertStatisticsToPlayerStatDto(response);
                    updatePlayerImage(playerId, playerStatDto.getImageUrl());
                    savePlayerStat(convertPlayerStatDtoToPlayerStat(playerId, playerStatDto));
                    return playerStatDto;
                });
    }

    public PlayerStatisticsApiResponseDto getSquadStatistics(Integer teamApiId, Integer leagueApiId, int currentSeason, int page) {
        return webClientService.getApiResponse(
                UrlEnum.buildPlayerStatisticsUrlByTeamApiId(teamApiId, leagueApiId, currentSeason, page),
                PlayerStatisticsApiResponseDto.class).block();
    }

    public Mono<List<TeamStandingDto>> getLeagueStandings(Integer leagueApiId, int currentSeason) {
        return webClientService.getApiResponse(
                UrlEnum.buildStandingUrl(leagueApiId, currentSeason),
                StandingsAPIResponseDto.class).mapNotNull(this::getValidatedLeagueDetails);
    }

    public List<PlayerSimpleDto> getLeagueTopScorers(Integer leagueApiId) {
        League league = findLeagueByLeagueApiId(leagueApiId);
        LeagueApiTopPlayerResponseDto response = webClientService.getApiResponse(
                UrlEnum.buildTopScorersUrl(leagueApiId, league.getCurrentSeason()),
                LeagueApiTopPlayerResponseDto.class).block();
        return convertToPlayerSimpleDtoList(response);
    }

    public List<PlayerSimpleDto> getLeagueTopAssists(Integer leagueApiId) {
        League league = findLeagueByLeagueApiId(leagueApiId);
        LeagueApiTopPlayerResponseDto response = webClientService.getApiResponse(
                UrlEnum.buildTopAssistsUrl(leagueApiId, league.getCurrentSeason()),
                LeagueApiTopPlayerResponseDto.class).block();
        return convertToPlayerSimpleDtoList(response);
    }

    public TeamStatisticsDto getTeamStatistics(Integer leagueApiId, Integer teamApiId, int currentSeason) {
        TeamStatisticsApiResponseDto response = webClientService.getApiResponse(
                UrlEnum.buildTeamStatisticsUrl(teamApiId, leagueApiId, currentSeason),
                TeamStatisticsApiResponseDto.class).block();
        return convertStatisticsToTeamDetailsDto(response, currentSeason);
    }

    public Mono<Optional<LeagueDetailsRequestDto>> getLeagueInfo(Integer leagueApiId) {
        return Objects.requireNonNull(webClientService.
                getApiResponse(UrlEnum.buildLeagueUrl(leagueApiId), LeagueApiResponseDto.class))
                .map(this::convertToLeagueInfoDto);
    }

    public List<SquadPlayerDto> getSquadPlayers(Integer teamApiId) {
        List<SquadApiResponseDto.ResponseItem> response = Objects.requireNonNull(webClientService.getApiResponse(
                UrlEnum.buildSquadUrl(teamApiId),
                SquadApiResponseDto.class).block()).getResponse();
        if (response == null || response.isEmpty() || response.get(0) == null) {
            return Collections.emptyList();
        }
        return response.getFirst().getPlayers()
                .stream().map(this::convertToSquadPlayerDto).collect(Collectors.toList());
    }

    private List<TeamStandingDto> getValidatedLeagueDetails(StandingsAPIResponseDto response) {
        if (response.getResults() <= 0) {
            return null;
        }
        return convertStandingToLeagueDetailsDto(response);
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

    private League findLeagueByLeagueApiId(Integer leagueApiId) {
        return leagueRepository.findLeagueByLeagueApiId(leagueApiId)
                .orElseThrow(() -> new IllegalArgumentException("League not found. leagueApiId: " + leagueApiId));
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
        playerStatDto.setRating(StringUtils.truncateToTwoDecimalsRanging(stat.getGames().getRating()));
        playerStatDto.setImageUrl(response.getResponse().getFirst().getPlayer().getPhoto());
        return playerStatDto;
    }

    private TeamStandingDto convertNullStandingDto(){
        return TeamStandingDto.builder()
                .build();
    }

    private List<TeamStandingDto> convertStandingToLeagueDetailsDto(StandingsAPIResponseDto response) {
        StandingsAPIResponseDto.League league = response.getResponse().getFirst().getLeague();
        return league.getStandings().getFirst().stream()
                .map(teamStandingDto ->{
                    return convertStandingToTeamStandingDto(teamStandingDto, league.getId());
                })
                .toList();
//        return LeagueStandingDto.builder()
//                .leagueApiId(league.getId())
//                .leagueName(league.getName())
//                .leagueLogo(league.getLogo())
//                .standings(teamStandingDtos)
//                .build();
    }

    private TeamStandingDto convertStandingToTeamStandingDto(StandingsAPIResponseDto.Standing standing, Integer leagueApiId) {
        return TeamStandingDto.builder()
                .LeagueApiId(leagueApiId)
                .rank(standing.getRank())
                .teamApiId(standing.getTeam().getId())
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

    private List<PlayerSimpleDto> convertToPlayerSimpleDtoList(LeagueApiTopPlayerResponseDto response) {
        return response.getResponse()
                .stream()
                .map((playerWrapper) -> PlayerSimpleDto.builder()
                        .teamApiId(playerWrapper.getStatistics().getFirst().getTeam().getId())
                        .playerApiId(playerWrapper.getPlayer().getId())
                        .name(playerWrapper.getPlayer().getName())
                        .teamName(playerWrapper.getStatistics().getFirst().getTeam().getName())
                        .age(playerWrapper.getPlayer().getAge())
                        .goal(playerWrapper.getStatistics().getFirst().getGoals().getTotal())
                        .assist(playerWrapper.getStatistics().getFirst().getGoals().getAssists())
                        .rating(playerWrapper.getStatistics().getFirst().getGames().getRating())
                        .imageUrl(playerWrapper.getPlayer().getPhoto())
                        .build()).toList();
    }

    private TeamStatisticsDto convertStatisticsToTeamDetailsDto(TeamStatisticsApiResponseDto response, int currentSeason) {
        TeamStatisticsApiResponseDto.Team team = response.getResponse().getTeam();
        TeamStatisticsApiResponseDto.League league = response.getResponse().getLeague();
        TeamStatisticsApiResponseDto.Fixtures fixtures = response.getResponse().getFixtures();
        return TeamStatisticsDto.builder()
                .teamName(team.getName())
                .teamApiId(team.getId())
                .logoImageUrl(team.getLogo())
                .leagueApiId(league.getId())
                .leagueName(league.getName())
                .leagueLogoImageUrl(league.getLogo())
                .nationName(league.getCountry())
                .nationLogoImageUrl(league.getFlag())
                .played(fixtures.getPlayed().getTotal())
                .wins(fixtures.getWins().getTotal())
                .draws(fixtures.getDraws().getTotal())
                .losses(fixtures.getLoses().getTotal())
                .currentSeason(currentSeason)
//                .goals(response.getResponse().getGoals().getGoalsFor().getTotal().getTotal())
//                .against(response.getResponse().getAgainst().getTotal().getTotal())
                .build();
    }

    private Optional<LeagueDetailsRequestDto> convertToLeagueInfoDto(LeagueApiResponseDto leagueApiResponseDto) {
        // 응답 리스트가 null이거나 비어 있으면 Optional.empty() 반환
        if (leagueApiResponseDto.response() == null || leagueApiResponseDto.response().isEmpty()) {
            return Optional.empty();
        }

        LeagueApiResponseDto.ResponseItem firstResponse = leagueApiResponseDto.response().getFirst();
        // 시즌 리스트가 null이거나 비어 있으면 Optional.empty() 반환
        if (firstResponse.seasons() == null || firstResponse.seasons().isEmpty()) {
            return Optional.empty();
        }
        LeagueApiResponseDto.League league = firstResponse.league();
        LeagueApiResponseDto.Country country = firstResponse.country();
        List<LeagueApiResponseDto.Season> seasons = firstResponse.seasons();
        LeagueApiResponseDto.Season season = seasons.getLast();
        LeagueDetailsRequestDto dto = LeagueDetailsRequestDto.builder()
                .leagueApiId(league.id())
                .currentSeason(season.year())
                .leagueName(league.name())
                .leagueType(league.type())
                .logoImageUrl(league.logo())
                .nationName(country.name())
                .nationImageUrl(country.flag())
                .Standing(season.coverage().standings())
                .build();
        return Optional.of(dto);
    }



    private SquadPlayerDto convertToSquadPlayerDto(SquadApiResponseDto.Player player) {
        return SquadPlayerDto.builder()
                .name(player.getName())
                .age(player.getAge())
                .imageUrl(player.getPhoto())
                .playerApiId(player.getId())
                .build();
    }
}