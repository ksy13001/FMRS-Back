package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.dto.PlayerDetailsResponseDto;
import com.ksy.fmrs.dto.SearchPlayerCondition;
import com.ksy.fmrs.dto.SearchPlayerResponseDto;
import com.ksy.fmrs.dto.TeamPlayersResponseDto;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerDetailsResponseDto getPlayerDetails(Long playerId) {
        return  playerRepository.findById(playerId)
                .map(PlayerDetailsResponseDto::new)
                .orElseThrow(()-> new IllegalArgumentException("Player not found : "+playerId));
    }

    public TeamPlayersResponseDto getTeamPlayersByTeamId(Long teamId) {
        return new TeamPlayersResponseDto(playerRepository.findAllByTeamId(teamId)
                .stream()
                .map(PlayerDetailsResponseDto::new)
                .toList());
    }

    public SearchPlayerResponseDto getPlayersByMarketValueDesc() {
        return new SearchPlayerResponseDto(playerRepository.findAllByOrderByMarketValueDesc()
                .stream()
                .map(PlayerDetailsResponseDto::new)
                .toList());
    }

    public SearchPlayerResponseDto searchPlayerByName(String name) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByName(name)
                .stream()
                .map(PlayerDetailsResponseDto::new)
                .toList());
    }

    public SearchPlayerResponseDto searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return new SearchPlayerResponseDto(playerRepository.searchPlayerByDetailCondition(condition)
                .stream()
                .map(PlayerDetailsResponseDto::new)
                .toList());
    }
}
