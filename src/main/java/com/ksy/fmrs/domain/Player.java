package com.ksy.fmrs.domain;


import com.ksy.fmrs.domain.enums.PositionEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "market_value")
    private int marketValue;

    @Enumerated(EnumType.STRING)
    private PositionEnum position;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String imageUrl;

    // 기술(Technical) 능력치
    private int corners;
    private int crossing;
    private int dribbling;
    private int finishing;
    @Column(name = "first_touch")
    private int firstTouch;
    @Column(name = "free_kick_taking")
    private int freeKickTaking;
    private int heading;
    @Column(name = "long_shots")
    private int longShots;
    @Column(name = "long_throws")
    private int longThrows;
    private int marking;
    private int passing;
    @Column(name = "penalty_taking")
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
    @Column(name = "off_the_ball")
    private int offTheBall;
    private int positioning;
    private int teamwork;
    private int vision;
    @Column(name = "work_rate")
    private int workRate;

    // 신체(Physical) 능력치
    private int acceleration;
    private int agility;
    private int balance;
    @Column(name = "jumping_reach")
    private int jumpingReach;
    @Column(name = "natural_fitness")
    private int naturalFitness;
    private int pace;
    private int stamina;
    private int strength;

    @Builder
    public Player(
            String name,
            LocalDate birth,
            int age,
            int height,
            int weight,
            int marketValue,
            PositionEnum positionEnum,
            String imageUrl,
            int corners,
            int crossing,
            int dribbling,
            int finishing,
            int firstTouch,
            int freeKickTaking,
            int heading,
            int longShots,
            int longThrows,
            int marking,
            int passing,
            int penaltyTaking,
            int tackling,
            int technique,
            int aggression,
            int anticipation,
            int bravery,
            int composure,
            int concentration,
            int decisions,
            int determination,
            int flair,
            int leadership,
            int offTheBall,
            int positioning,
            int teamwork,
            int vision,
            int workRate,
            int acceleration,
            int agility,
            int balance,
            int jumpingReach,
            int naturalFitness,
            int pace,
            int stamina,
            int strength
    ) {
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.marketValue = marketValue;
        this.position = positionEnum;
        this.imageUrl = imageUrl;

        this.corners = corners;
        this.crossing = crossing;
        this.dribbling = dribbling;
        this.finishing = finishing;
        this.firstTouch = firstTouch;
        this.freeKickTaking = freeKickTaking;
        this.heading = heading;
        this.longShots = longShots;
        this.longThrows = longThrows;
        this.marking = marking;
        this.passing = passing;
        this.penaltyTaking = penaltyTaking;
        this.tackling = tackling;
        this.technique = technique;

        this.aggression = aggression;
        this.anticipation = anticipation;
        this.bravery = bravery;
        this.composure = composure;
        this.concentration = concentration;
        this.decisions = decisions;
        this.determination = determination;
        this.flair = flair;
        this.leadership = leadership;
        this.offTheBall = offTheBall;
        this.positioning = positioning;
        this.teamwork = teamwork;
        this.vision = vision;
        this.workRate = workRate;

        this.acceleration = acceleration;
        this.agility = agility;
        this.balance = balance;
        this.jumpingReach = jumpingReach;
        this.naturalFitness = naturalFitness;
        this.pace = pace;
        this.stamina = stamina;
        this.strength = strength;
    }

    // 연관관계 설정 메서드
    public void updateTeam(Team team) {
        this.team = team;
        team.getPlayers().add(this);
    }

    public void updateMarketValue(int marketValue) {
        this.marketValue = marketValue;
    }
}
