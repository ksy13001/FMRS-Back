package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "player_stat")
public class PlayerStat extends BaseTime {

    @Id
    @Column(name = "player_id")
    private Long playerId;

    private Integer gamesPlayed;
    private Integer substitutes;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private Integer yellowCards;
    private Integer redCards;
    private String rating;


    @Builder
    public PlayerStat(Player player, Long playerId, Integer gamesPlayed, Integer substitutes,
                      Integer goal, Integer pk, Integer assist,
                      String rating, Integer yellowCards, Integer redCards) {
        this.gamesPlayed = gamesPlayed;
        this.substitutes = substitutes;
        this.goal = goal;
        this.pk = pk;
        this.assist = assist;
        this.rating = rating;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    public boolean isExpired(LocalDateTime now){
        if(Duration.between(this.getModifiedDate(), now).toHours() >= 24){
            return true;
        }
        return false;
    }

    public void updatePlayerStat(Integer gamesPlayed, Integer substitutes, Integer goal, Integer pk,
                                 Integer assist, String rating, Integer yellowCards, Integer redCards){
        this.gamesPlayed = gamesPlayed;
        this.substitutes = substitutes;
        this.goal = goal;
        this.pk = pk;
        this.assist = assist;
        this.rating = rating;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }
}
