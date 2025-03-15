package com.ksy.fmrs.domain.player;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Position {
    private int goalkeeper;
    private int defenderCentral;
    private int defenderLeft;
    private int defenderRight;
    private int wingBackLeft;
    private int wingBackRight;
    private int defensiveMidfielder;
    private int midfielderLeft;
    private int midfielderRight;
    private int midfielderCentral;
    private int attackingMidCentral;
    private int attackingMidLeft;
    private int attackingMidRight;
    private int striker;

}
//GK("Goalkeeper"),
//CB("DefenderCentral"),
//LB("DefenderLeft"),
//RB("DefenderRight"),
//WBL("WingBackLeft"),
//WBR("WingBackRight"),
//DM("DefensiveMidfielder"),
//LM("MidfielderLeft"),
//RM("MidfielderRight"),
//CM("MidfielderCentral"),
//AM("AttackingMidCentral"),
//LW("AttackingMidLeft"),
//RW("AttackingMidRight"),
//ST("Striker");
