package com.ksy.fmrs.domain;

import com.ksy.fmrs.domain.enums.LeagueType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "league_api_id", unique = true)
    Integer leagueApiId;

    private String name;

    @Column(name = "nation_name")
    private String nationName;

    @Column(name = "nation_logo_url")
    private String nationLogoUrl;

    @Column(name = "current_season")
    private Integer currentSeason;

    private String logoUrl;

    @Column(name = "league_type")
    @Enumerated(EnumType.STRING)
    private LeagueType leagueType;

    @OneToMany(mappedBy = "league")
    private List<Team> teams = new ArrayList<>();

    private Boolean standing;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public League(Integer leagueApiId,
                  String name,
                  String nationName,
                  String nationLogoUrl,
                  Integer currentSeason,
                  String logoUrl,
                  LeagueType leagueType,
                  Boolean standing,
                  LocalDate startDate,
                  LocalDate endDate) {
        this.leagueApiId = leagueApiId;
        this.name = name;
        this.nationName = nationName;
        this.nationLogoUrl = nationLogoUrl;
        this.currentSeason = currentSeason;
        this.logoUrl = logoUrl;
        this.leagueType = leagueType;
        this.standing = standing;
        this.startDate = startDate;
        this.endDate = endDate;

    }


    public void updateLeagueInfo(Integer leagueApiId, String name, String nation, String nationImageUrl, Integer currentSeason, String logoUrl) {
        this.leagueApiId = leagueApiId;
        this.name = name;
        this.nationName = nation;
        this.nationLogoUrl = nationImageUrl;
        this.currentSeason = currentSeason;
        this.logoUrl = logoUrl;
    }

    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void updateCurrentSeason(Integer currentSeason) {
        this.currentSeason = currentSeason;
    }
}
