package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.player.PlayerStat;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PlayerStatDto {
    private Integer gamesPlayed;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;

    public PlayerStatDto(PlayerStat playerStat) {
        this.gamesPlayed = playerStat.getGamesPlayed();
        this.assist = playerStat.getAssist();
        this.goal = playerStat.getGoal();
        this.pk = playerStat.getPk();
        this.rating = playerStat.getRating();
    }
}
