package com.ksy.fmrs.domain;

import jakarta.persistence.*;

@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;
}
