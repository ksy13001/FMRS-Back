package com.ksy.fmrs.domain.player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Position {

    private int goalkeeper;

    @Column(name = "defender_central")
    private int defenderCentral;

    @Column(name = "defender_left")
    private int defenderLeft;

    @Column(name = "defender_right")
    private int defenderRight;

    @Column(name = "wing_back_left")
    private int wingBackLeft;

    @Column(name = "wing_back_right")
    private int wingBackRight;

    @Column(name = "defensive_midfielder")
    private int defensiveMidfielder;

    @Column(name = "midfielder_left")
    private int midfielderLeft;

    @Column(name = "midfielder_right")
    private int midfielderRight;

    @Column(name = "midfielder_central")
    private int midfielderCentral;

    @Column(name = "attacking_mid_central")
    private int attackingMidCentral;

    @Column(name = "attacking_mid_left")
    private int attackingMidLeft;

    @Column(name = "attacking_mid_right")
    private int attackingMidRight;

    private int striker;
}
