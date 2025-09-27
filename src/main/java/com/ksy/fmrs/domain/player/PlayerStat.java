package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "player_stat")
public class PlayerStat extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer gamesPlayed;
    private Integer substitutes;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private Integer yellowCards;
    private Integer redCards;
    @Column(length = 10)
    private String rating;


    @Builder
    public PlayerStat(Integer gamesPlayed, Integer substitutes,
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

    public boolean isExpired(Instant now, Duration ttl){
        return !this.getModifiedDate().isAfter(now.minus(ttl));
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
