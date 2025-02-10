package com.ksy.fmrs.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Nation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long division;

    @OneToMany(mappedBy = "nation", fetch = FetchType.LAZY)
    private List<League> leagues = new ArrayList<>();
}
