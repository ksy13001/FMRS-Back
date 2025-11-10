package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.player.PlayerStat;
import com.ksy.fmrs.dto.apiFootball.ApiFootballPlayersStatistics;
import com.ksy.fmrs.dto.apiFootball.PlayerStatisticApiDto;
import org.springframework.stereotype.Component;

@Component
public class PlayerStatMapper {

    public PlayerStat toEntity(ApiFootballPlayersStatistics dto) {
        if(dto == null ||
                dto.response() == null ||
                dto.response().isEmpty()) {
            return null;
        }
        ApiFootballPlayersStatistics.StatisticDto statistic = dto.response().getFirst().statistics().getFirst();
        return PlayerStat.builder()
                .gamesPlayed(statistic.games().appearences())
                .substitutes(statistic.substitutes().in())
                .goal(statistic.goals().total())
                .assist(statistic.goals().assists())
                .pk(statistic.penalty().scored())
                .rating(statistic.games().rating())
                .yellowCards(statistic.cards().yellow())
                .redCards(statistic.cards().red())
                .build();
    }
}

