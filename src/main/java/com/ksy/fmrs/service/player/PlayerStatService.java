package com.ksy.fmrs.service.player;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.player.PlayerStatDto;
import com.ksy.fmrs.mapper.PlayerStatMapper;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerStatRepository;
import com.ksy.fmrs.service.global.FootballApiService;
import com.ksy.fmrs.util.time.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Player 통해서 playerStat 조회
     * PlayerStat 이 존재 하고 수정 시각이 하루 미만일 경우 조회한 PlayerStat 사용
     * 하루 이상일 경우 외부 api로 값 가져 오고 PlayerStat 업데이트
     * PlayerStat이 존재 하지 않을 경우 외부 api로 값 가져 오기
     * 팀 매핑된 경우에만 스탯 가져올수있음
     */
    @Transactional
    public Optional<PlayerStatDto> saveAndGetPlayerStat(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(()-> new EntityNotFoundException("Player not found with id: " + playerId));
        PlayerStat playerStat = player.getPlayerStat();

        if (playerStat == null || playerStat.isExpired(timeProvider.getCurrentLocalDateTime())) {
            return savePlayerStat(player).map(PlayerStatDto::new);
        }
        return Optional.of(new PlayerStatDto(playerStat));
    }

    private Optional<PlayerStat> savePlayerStat(Player player) {
        Team team = player.getTeam();
        if (team == null) {
            return Optional.empty();
        }
        League league = team.getLeague();
        PlayerStat ps = playerStatMapper.toEntity(
                footballApiService.getPlayerStatByPlayerApiIdAndTeamApiIdAndLeagueApiId(
                        player.getPlayerApiId(),
                        team.getTeamApiId(),
                        league.getLeagueApiId(),
                        league.getCurrentSeason()));
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
