package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.mapper.PlayerStatMapper;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PlayerStatService {

    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;
    private final FootballApiService footballApiService;
    private final PlayerStatMapper playerStatMapper;
    private final TimeProvider timeProvider;

    /**
     * PlayerStat 이 존재 하고 수정 시각이 하루 미만일 경우 조회한 PlayerStat 사용
     * 하루 이상일 경우 외부 api로 값 가져 오고 PlayerStat 업데이트
     * PlayerStat이 존재 하지 않을 경우 외부 api로 값 가져 오기
     */

    @Transactional
    public Optional<PlayerStatDto> saveAndGetPlayerStat(Long playerId) {
        PlayerStat playerStat = playerStatRepository.findById(playerId)
                .filter(ps -> !ps.isExpired(timeProvider.getCurrentTime()))
                .orElseGet(() -> savePlayerStat(playerId));
        if (playerStat == null) {
            return Optional.empty();
        }
        return Optional.of(new PlayerStatDto(playerStat));
    }

    private PlayerStat savePlayerStat(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(EntityNotFoundException::new);
        Team team = player.getTeam();
        if (team == null) {
            return null;
        }
        League league = team.getLeague();
        PlayerStat ps = playerStatMapper.toEntity(
                footballApiService.getPlayerStatByPlayerApiIdAndTeamApiIdAndLeagueApiId(
                        player.getPlayerApiId(),
                        team.getTeamApiId(),
                        league.getLeagueApiId(),
                        league.getCurrentSeason()));
        playerStatRepository.save(ps);
        return ps;
    }

    @Transactional(readOnly = true)
    public PlayerStatDto getPlayerStatByPlayerId(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found. id: " + playerId));
        return new PlayerStatDto(player.getPlayerStat());
    }

}
