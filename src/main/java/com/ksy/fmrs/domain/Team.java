package com.ksy.fmrs.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @ManyToOne
    private League league;
}
