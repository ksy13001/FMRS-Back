package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticApiDto;
import org.springframework.stereotype.Component;

@Component
public class PlayerStatMapper {

    public PlayerStat StatisticApiDtoToPlayerStat(Long playerId, PlayerStatisticApiDto playerStatisticApiDto) {
        PlayerStatisticApiDto.Statistic statistic = playerStatisticApiDto.response().getFirst().statistics().getFirst();
        return PlayerStat.builder()
                .playerId(playerId)
                .gamesPlayed(statistic.games().appearences())
                .substitutes(statistic.substitutes().in())
                .goal(statistic.goals().total())
                .assist(statistic.goals().assists())
                .pk(statistic.penalty().won())
                .rating(statistic.games().rating())
                .yellowCards(statistic.cards().yellow())
                .redCards(statistic.cards().red())
                .build();
    }
}

