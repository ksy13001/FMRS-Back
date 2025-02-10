package com.ksy.fmrs.domain;


import com.ksy.fmrs.domain.enums.Position;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birth;

    private int age;

    private int height;

    private int weight;

    private String nationality;

    private Position position;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
