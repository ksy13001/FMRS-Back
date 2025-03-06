package com.ksy.fmrs.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PhysicalAttributes {
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
}