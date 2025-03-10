package com.ksy.fmrs.domain;

import com.ksy.fmrs.domain.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer teamApiId;

    private String name;

    private String logoUrl;

    private String nationName;

    private String nationLogoUrl;

    private int currentSeason;

    private Integer played;

    private Integer won;

    private Integer drawn;

    private Integer lost;

    private Integer goalsFor;

    private Integer goalsAgainst;

    private Integer points;

    private Integer goalsDifference;

    private String description;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private League league;

    @Builder
    public Team(String name, Integer teamApiId, String logoUrl, String nationName, String nationLogoImageUrl, int currentSeason) {
        this.name = name;
        this.teamApiId = teamApiId;
        this.logoUrl = logoUrl;
        this.nationName = nationName;
        this.nationLogoUrl = nationLogoImageUrl;
        this.currentSeason = currentSeason;
    }

    public void updateLeague(League league) {
        this.league = league;
        league.getTeams().add(this);
    }

    public void resetSquad(){
        this.players = new ArrayList<>();
    }
}
