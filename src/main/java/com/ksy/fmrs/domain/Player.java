package com.ksy.fmrs.domain;


import com.ksy.fmrs.domain.enums.Position;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate birth;

    private int age;

    private int height;

    private int weight;

    @Enumerated(EnumType.STRING)
    private Position position;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;


    // 기술(Technical) 능력치
    private int corners;
    private int crossing;
    private int dribbling;
    private int finishing;
    private int firstTouch;
    private int freeKickTaking;
    private int heading;
    private int longShots;
    private int longThrows;
    private int marking;
    private int passing;
    private int penaltyTaking;
    private int tackling;
    private int technique;

    // 정신(Mental) 능력치
    private int aggression;
    private int anticipation;
    private int bravery;
    private int composure;
    private int concentration;
    private int decisions;
    private int determination;
    private int flair;
    private int leadership;
    private int offTheBall;
    private int positioning;
    private int teamwork;
    private int vision;
    private int workRate;

    // 신체(Physical) 능력치
    private int acceleration;
    private int agility;
    private int balance;
    private int jumpingReach;
    private int naturalFitness;
    private int pace;
    private int stamina;
    private int strength;
}
