package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.player.*;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticsApiResponseDto;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.dto.search.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.team.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    /**
     * 선수 상세 정보 조회
     */
    public PlayerDetailsDto getPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return convertPlayerToPlayerDetailsResponseDto(player);
    }

    public void saveAllByPlayerStatistics(PlayerStatisticsApiResponseDto playerStatisticsApiResponseDto) {
        List<Player> players = playerStatisticsApiResponseDto.response().stream().filter(Objects::nonNull)
                .map(dto -> {
                    PlayerStatisticsApiResponseDto.PlayerDto player = dto.player();
                    PlayerStatisticsApiResponseDto.StatisticDto.TeamDto teamDto = dto.statistics().getFirst().team();
                    Team team = teamRepository.findTeamByTeamApiId(teamDto.id())
                            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamDto.id()));
                    Player newPlayer = Player.builder()
                            .name(player.name())
                            .playerApiId(player.id())
                            .teamApiId(Objects.requireNonNull(dto.statistics()).getFirst().team().id())
                            .leagueApiId(Objects.requireNonNull(dto.statistics()).getFirst().league().id())
                            .firstName(StringUtils.getFirstName(player.firstname()).toUpperCase())
                            .lastName(StringUtils.getLastName(player.lastname()).toUpperCase())
                            .nationName(player.nationality().toUpperCase())
                            .nationLogoUrl(Objects.requireNonNull(dto.statistics().getFirst().league().flag()))
                            .age(player.age())
                            .birth(player.birth().date())
                            .height(StringUtils.extractNumber(player.height()))
                            .weight(StringUtils.extractNumber(player.weight()))
                            .build();
                    newPlayer.updateTeam(team);
                    return newPlayer;
                }).toList();
        playerRepository.saveAll(players);
    }

    public void saveAll(List<Player> players) {
        // 중복 제거된 Player 리스트를 저장
        playerRepository.saveAll(getDistinctPlayersByPlayerApiId(players));
    }

    private List<Player> getDistinctPlayersByPlayerApiId(List<Player> players) {
        return players.stream()
                .collect(Collectors.toMap(
                        Player::getPlayerApiId,          // keyMapper: playerApiId를 key 로
                        Function.identity(),             // valueMapper: Player 객체 자체
                        (existing, replacement) -> existing // mergeFunction: key 충돌 시 기존 객체 사용
                ))
                .values()
                .stream()
                .toList();
    }

    private PlayerDetailsDto convertPlayerToPlayerDetailsResponseDto(Player player) {
        return new PlayerDetailsDto(player, getTeamNameByPlayer(player));
    }

    private String getTeamNameByPlayer(Player player) {
        return Optional.ofNullable(player.getTeam())
                .map(Team::getName)
                .orElse(null);
    }

    @Transactional
    public void updatePlayerApiIdByPlayerWrapperDto(Integer playerApiId, String firstName, String lastName, LocalDate birth, String nationName) {
        List<Player> findPlayers = playerRepository.searchPlayerByFm(firstName, lastName, birth, nationName);
        if (findPlayers.size() > 1) {
            return;
        }
        if (findPlayers.isEmpty()) {
            return;
        }
        findPlayers.getFirst().updatePlayerApiId(playerApiId);
    }

    /**
     * 팀 소속 선수들 모두 조회
     */
    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

//    /**
//     * 모든 선수 몸값순 조회
//     */
//    public SearchPlayerResponseDto getPlayersByMarketValueDesc() {
//        return new SearchPlayerResponseDto(playerRepository.findAllByOrderByMarketValueDesc()
//                .stream()
//                .map(this::convertPlayerToPlayerDetailsResponseDto)
//                .toList());
//    }


    /**
     * 선수 이름 검색
     */
    public SearchPlayerResponseDto searchPlayerByName(String name) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByName(name)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 선수 상세 조회
     */
    public SearchPlayerResponseDto searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }
}
