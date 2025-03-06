package com.ksy.fmrs.domain.player;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "PlayerStat", timeToLive = 86400)
public class PlayerStat {
    @Id
    private Long playerId;
    private Integer apiFootballId;
    private Integer gamesPlayed;
    private Integer goal;
    private Integer pk;
    private Integer assist;
    private String rating;
    private String imageUrl;

    @Builder
    public PlayerStat(Long playerId, Integer apiFootballId, Integer gamesPlayed, Integer goal, Integer pk, Integer assist, String rating, String imageUrl) {
        this.playerId = playerId;
        this.apiFootballId = apiFootballId;
        this.gamesPlayed = gamesPlayed;
        this.goal = goal;
        this.pk = pk;
        this.assist = assist;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }
}
