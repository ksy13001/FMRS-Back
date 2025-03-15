package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.domain.enums.PositionEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPlayerCondition {
    // 선수 검색 조건 - 이름, 나이,

    private String name;
    private Integer ageMin;
    private Integer ageMax;
    private PositionEnum positionEnum;
    private String teamName;
    private String leagueName;
    private String nationName;

    // 기술(Technical) 능력치 (Min, Max)
    private Integer corners;
    private Integer crossing;
    private Integer dribbling;
    private Integer finishing;
    private Integer firstTouch;
    private Integer freeKickTaking;
    private Integer heading;
    private Integer longShots;
    private Integer longThrows;
    private Integer marking;
    private Integer passing;
    private Integer penaltyTaking;
    private Integer tackling;
    private Integer technique;

    // 정신(Mental) 능력치 (Min, Max)
    private Integer aggression;
    private Integer anticipation;
    private Integer bravery;
    private Integer composure;
    private Integer concentration;
    private Integer decisions;
    private Integer determination;
    private Integer flair;
    private Integer leadership;
    private Integer offTheBall;
    private Integer positioning;
    private Integer teamwork;
    private Integer vision;
    private Integer workRate;

    // 신체(Physical) 능력치 (Min, Max)
    private Integer acceleration;
    private Integer agility;
    private Integer balance;
    private Integer jumpingReach;
    private Integer naturalFitness;
    private Integer pace;
    private Integer stamina;
    private Integer strength;
}
