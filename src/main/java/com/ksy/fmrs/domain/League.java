package com.ksy.fmrs.domain;

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
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Integer leagueApiId;

    private String name;

    private String nation;

    private String nationImageUrl;

    private Integer currentSeason;

    private String logoUrl;

    @Builder
    public League(Integer leagueApiId, String name, String nation, String nationImageUrl, Integer currentSeason, String logoUrl) {
        this.leagueApiId = leagueApiId;
        this.name = name;
        this.nation = nation;
        this.nationImageUrl = nationImageUrl;
        this.currentSeason = currentSeason;
        this.logoUrl = logoUrl;
    }

    public void updateLeagueInfo(Integer leagueApiId, String name, String nation, String nationImageUrl, Integer currentSeason, String logoUrl) {
        this.leagueApiId = leagueApiId;
        this.name = name;
        this.nation = nation;
        this.nationImageUrl = nationImageUrl;
        this.currentSeason = currentSeason;
        this.logoUrl = logoUrl;
    }
}
