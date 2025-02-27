package com.ksy.fmrs.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Integer leagueApiId;

    private String name;

    private int division;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @Builder
    public League(String name, int division) {
        this.name = name;
        this.division = division;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
