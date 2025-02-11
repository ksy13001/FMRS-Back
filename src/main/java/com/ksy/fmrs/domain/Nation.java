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
public class Nation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "nation", fetch = FetchType.LAZY)
    private List<Player> players;

    @OneToMany(mappedBy = "nation", fetch = FetchType.LAZY)
    private List<League> leagues = new ArrayList<>();

    @Builder
    public Nation(String name) {
        this.name = name;
    }
}
