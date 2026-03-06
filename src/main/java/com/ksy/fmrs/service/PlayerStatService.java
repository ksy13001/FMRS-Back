package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.domain.enums.StatFreshness;
import com.ksy.fmrs.dto.player.PlayerStatResponse;
import com.ksy.fmrs.mapper.PlayerStatMapper;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import com.ksy.fmrs.util.PlayerStatTtlProvider;
import com.ksy.fmrs.util.time.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlayerStatService {

    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;
    private final ApiFootballClient apiFootballClient;
    private final PlayerStatMapper playerStatMapper;
    private final TimeProvider timeProvider;
    private final PlayerStatTtlProvider ttlProvider;

    @Transactional(readOnly = true)
    public PlayerStatResponse getPlayerStatById(Long playerId) {
        Player player = playerRepository.findWithPlayerStatById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with id: " + playerId));
        return getPlayerStatResponse(player);
    }

    @Transactional
    public PlayerStatResponse savePlayerStat(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with id: " + playerId));
        Team team = Optional.ofNullable(player.getTeam())
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));
        League league = Optional.ofNullable(team.getLeague())
                .orElseThrow(() -> new EntityNotFoundException("League not found"));

        PlayerStat ps = playerStatMapper.toEntity(
                apiFootballClient.requestPlayerStatistics(
                        league.getLeagueApiId(),
                        team.getTeamApiId(),
                        player.getPlayerApiId(),
                        league.getCurrentSeason()));

        player.updatePlayerStat(ps);
        playerStatRepository.save(ps);
        return PlayerStatResponse.fresh(ps);
    }


    @Transactional(readOnly = true)
    public PlayerStatResponse getPlayerStatByPlayerId(Long playerId) {
        return getPlayerStatById(playerId);
    }

    public PlayerStatResponse buildPlayerStatResponse(Player player) {
        return getPlayerStatResponse(player);
    }

    private PlayerStatResponse getPlayerStatResponse(Player player) {
        StatFreshness freshness = player.getPlayerStatFreshness(timeProvider.getCurrentInstant(), ttlProvider.getTtl());
        PlayerStat playerStat = player.getPlayerStat();
        return switch (freshness) {
            case MISSING -> PlayerStatResponse.missing();
            case EXPIRED -> PlayerStatResponse.expired(playerStat);
            case FRESH -> PlayerStatResponse.fresh(playerStat);
        };
    }

}
