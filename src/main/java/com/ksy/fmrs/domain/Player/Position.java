package com.ksy.fmrs.domain.Player;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Position {
    private int Goalkeeper;
    private int DefenderCentral;
    private int DefenderLeft;
    private int DefenderRight;
    private int WingBackLeft;
    private int WingBackRight;
    private int DefensiveMidfielder;
    private int MidfielderLeft;
    private int MidfielderRight;
    private int MidfielderCentral;
    private int AttackingMidCentral;
    private int AttackingMidLeft;
    private int AttackingMidRight;
    private int Striker;

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
