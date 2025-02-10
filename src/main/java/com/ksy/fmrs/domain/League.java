package com.ksy.fmrs.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int division;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;
}
