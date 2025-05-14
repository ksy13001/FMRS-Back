package com.ksy.fmrs.domain.enums;

public enum PositionEnum {
//    GK, CB, LB, RB, DM, CM, AM, LM, RM, LW, RW, ST
    GK("Goalkeeper"),
    CB("DefenderCentral"),
    LB("DefenderLeft"),
    RB("DefenderRight"),
    WBL("WingBackLeft"),
    WBR("WingBackRight"),
    DM("DefensiveMidfielder"),
    LM("MidfielderLeft"),
    RM("MidfielderRight"),
    CM("MidfielderCentral"),
    AM("AttackingMidCentral"),
    LW("AttackingMidLeft"),
    RW("AttackingMidRight"),
    ST("Striker");

    private final String value;
    PositionEnum(String value) {
        this.value = value;
    }
    String getValue() {
        return value;
    }

    // String 값으로부터 Enum 변환 (매핑)
    public static PositionEnum fromString(String position) {
        for (PositionEnum pos : PositionEnum.values()) {
            if (pos.getValue().equalsIgnoreCase(position)) {
                return pos;
            }
        }
        throw new IllegalArgumentException("Unknown position: " + position);
    }
}