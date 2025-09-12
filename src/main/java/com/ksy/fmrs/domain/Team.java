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

    @Column(name = "team_api_id",  unique = true)
    private Integer teamApiId;

    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    private boolean national;

    private Integer played;

    private Integer won;

    private Integer drawn;

    private Integer lost;

    private Integer goalsFor;

    private Integer goalsAgainst;

    private Integer points;

    private Integer goalsDifference;

    private String description;

    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private League league;

    @Builder
    public Team(String name, Integer teamApiId, String logoUrl) {
        this.name = name;
        this.teamApiId = teamApiId;
        this.logoUrl = logoUrl;
    }

    public void updateLeague(League league) {
        this.league = league;
        league.getTeams().add(this);
    }

    public void resetSquad(){
        this.players = new ArrayList<>();
    }
}
