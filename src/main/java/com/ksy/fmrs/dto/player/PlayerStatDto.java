package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.player.PlayerStat;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PlayerStatDto {
    private Integer gamesPlayed;
    private Integer substitutes;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;
    private Integer yellowCards;
    private Integer redCards;

    public PlayerStatDto(PlayerStat playerStat) {
        this.gamesPlayed = playerStat.getGamesPlayed();
        this.substitutes = playerStat.getSubstitutes();
        this.assist = playerStat.getAssist();
        this.goal = playerStat.getGoal();
        this.pk = playerStat.getPk();
        this.rating = playerStat.getRating();
        this.yellowCards = playerStat.getYellowCards();
        this.redCards = playerStat.getRedCards();
    }
}
