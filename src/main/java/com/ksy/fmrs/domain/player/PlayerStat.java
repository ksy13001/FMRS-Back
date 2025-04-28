package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PlayerStat extends BaseTime {

    @Id
    @Column(name = "player_id")
    private Long playerId;

    private Integer apiFootballId;
    private Integer gamesPlayed;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    @Builder
    public PlayerStat(Long playerId, Integer apiFootballId, Integer gamesPlayed, Integer goal, Integer pk, Integer assist, String rating) {
        this.playerId = playerId;
        this.apiFootballId = apiFootballId;
        this.gamesPlayed = gamesPlayed;
        this.goal = goal;
        this.pk = pk;
        this.assist = assist;
        this.rating = rating;
    }
}
