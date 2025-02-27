package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Nation;
import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.*;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * 선수 상세 정보 조회
     * */
    public PlayerDetailsDto getPlayerDetails(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
        return convertPlayerToPlayerDetailsResponseDto(player);
    }

    private PlayerDetailsDto convertPlayerToPlayerDetailsResponseDto(Player player) {
        return new PlayerDetailsDto(player,  getTeamNameByPlayer(player), getNationNameByPlayer(player));
    }

    private String getTeamNameByPlayer(Player player) {
        return Optional.ofNullable(player.getTeam())
                .map(Team::getName)
                .orElse(null);
    }

    private String getNationNameByPlayer(Player player) {
        return Optional.ofNullable(player.getNation())
                .map(Nation::getName)
                .orElse(null);
    }

    /**
     * 팀 소속 선수들 모두 조회
     * */
    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 모든 선수 몸값순 조회
     * */
    public SearchPlayerResponseDto getPlayersByMarketValueDesc() {
        return new SearchPlayerResponseDto(playerRepository.findAllByOrderByMarketValueDesc()
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }


    /**
     * 선수 이름 검색
     * */
    public SearchPlayerResponseDto searchPlayerByName(String name) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByName(name)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }

    /**
     * 선수 상세 조회
     * */
    public SearchPlayerResponseDto searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(this::convertPlayerToPlayerDetailsResponseDto)
                .toList());
    }
}
