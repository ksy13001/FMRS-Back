package com.ksy.fmrs.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Nation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long division;

    @OneToMany(mappedBy = "nation", fetch = FetchType.LAZY)
    private List<Player> players;

    @OneToMany(mappedBy = "nation", fetch = FetchType.LAZY)
    private List<League> leagues = new ArrayList<>();
}
