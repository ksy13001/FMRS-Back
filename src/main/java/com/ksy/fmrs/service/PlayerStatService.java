package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.player.PlayerStatDto;
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

    // 트랜잭션내의 외부 api 호출 분리하기
    @Transactional
    public Optional<PlayerStatDto> saveAndGetPlayerStat(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(()-> new EntityNotFoundException("Player not found with id: " + playerId));
        PlayerStat playerStat = player.getPlayerStat();

        if (player.needsStatRefresh(timeProvider.getCurrentInstant(), ttlProvider.getTtl())) {
            return savePlayerStat(player).map(PlayerStatDto::new);
        }

        return Optional.of(new PlayerStatDto(playerStat));
    }

    private Optional<PlayerStat> savePlayerStat(Player player) {

        Team team = Optional.ofNullable(player.getTeam())
                .orElseThrow(()-> new EntityNotFoundException("Team not found"));
        League league = Optional.ofNullable(team.getLeague())
                .orElseThrow(()-> new EntityNotFoundException("League not found"));
        PlayerStat ps = playerStatMapper.toEntity(
                apiFootballClient.requestPlayerStatistics(
                        league.getLeagueApiId(),
                        team.getTeamApiId(),
                        player.getPlayerApiId(),
                        league.getCurrentSeason()));

        log.info("-------------savePlayerStat: league_api_id={}, team_api_id={}, player_api_id={}, currentSeason={}",
                league.getLeagueApiId(), team.getTeamApiId(), player.getPlayerApiId(), league.getLeagueApiId());
        if(ps == null) {
            return Optional.empty();
        }
        player.updatePlayerStat(ps);
        playerStatRepository.save(ps);
        return Optional.of(ps);
    }

    @Transactional(readOnly = true)
    public PlayerStatDto getPlayerStatByPlayerId(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found. id: " + playerId));
        return new PlayerStatDto(player.getPlayerStat());
    }

}
