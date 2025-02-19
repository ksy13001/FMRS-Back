package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.PlayerStat;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PlayerStatDto {
    private Integer apiFootballId;
    private Integer gamesPlayed;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;
    private String imageUrl;

    public PlayerStatDto(PlayerStat playerStat) {
        this.assist = playerStat.getAssist();
        this.gamesPlayed = playerStat.getGamesPlayed();
        this.goal = playerStat.getGoal();
        this.pk = playerStat.getPk();
        this.rating = playerStat.getRating();
        this.imageUrl = playerStat.getImageUrl();
    }
}
