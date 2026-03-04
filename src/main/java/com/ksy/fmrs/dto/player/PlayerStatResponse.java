package com.ksy.fmrs.dto.player;

import com.ksy.fmrs.domain.enums.StatFreshness;
import com.ksy.fmrs.domain.player.PlayerStat;
import lombok.Data;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor(access = PRIVATE)
public class PlayerStatResponse {
    private Integer gamesPlayed;
    private Integer substitutes;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;
    private Integer yellowCards;
    private Integer redCards;
    private StatFreshness statFreshness;
    private boolean needsStatRefresh;

    private static PlayerStatResponse from(PlayerStat playerStat, StatFreshness freshness, boolean needsRefresh) {
        PlayerStatResponse response = new PlayerStatResponse();
        if (playerStat != null) {
            response.gamesPlayed = playerStat.getGamesPlayed();
            response.substitutes = playerStat.getSubstitutes();
            response.assist = playerStat.getAssist();
            response.goal = playerStat.getGoal();
            response.pk = playerStat.getPk();
            response.rating = playerStat.getRating();
            response.yellowCards = playerStat.getYellowCards();
            response.redCards = playerStat.getRedCards();
        }
        response.statFreshness = freshness;
        response.needsStatRefresh = needsRefresh;
        return response;
    }

    public static PlayerStatResponse fresh(PlayerStat playerStat) {
        return from(playerStat, StatFreshness.FRESH, false);
    }

    public static PlayerStatResponse expired(PlayerStat playerStat) {
        return from(playerStat, StatFreshness.EXPIRED, true);
    }

    public static PlayerStatResponse missing() {
        return from(null, StatFreshness.MISSING, true);
    }
}
