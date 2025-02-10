package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.enums.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerSearchCondition {
    // 선수 검색 조건 - 이름, 나이,

    private String name;
    private Integer ageMin;
    private Integer ageMax;
    private Position position;
    private String teamName;
    private String leagueName;
    private String nationName;

    // 기술(Technical) 능력치 (Min, Max)
    private Integer cornersMin;
    private Integer cornersMax;
    private Integer crossingMin;
    private Integer crossingMax;
    private Integer dribblingMin;
    private Integer dribblingMax;
    private Integer finishingMin;
    private Integer finishingMax;
    private Integer firstTouchMin;
    private Integer firstTouchMax;
    private Integer freeKickTakingMin;
    private Integer freeKickTakingMax;
    private Integer headingMin;
    private Integer headingMax;
    private Integer longShotsMin;
    private Integer longShotsMax;
    private Integer longThrowsMin;
    private Integer longThrowsMax;
    private Integer markingMin;
    private Integer markingMax;
    private Integer passingMin;
    private Integer passingMax;
    private Integer penaltyTakingMin;
    private Integer penaltyTakingMax;
    private Integer tacklingMin;
    private Integer tacklingMax;
    private Integer techniqueMin;
    private Integer techniqueMax;

    // 정신(Mental) 능력치 (Min, Max)
    private Integer aggressionMin;
    private Integer aggressionMax;
    private Integer anticipationMin;
    private Integer anticipationMax;
    private Integer braveryMin;
    private Integer braveryMax;
    private Integer composureMin;
    private Integer composureMax;
    private Integer concentrationMin;
    private Integer concentrationMax;
    private Integer decisionsMin;
    private Integer decisionsMax;
    private Integer determinationMin;
    private Integer determinationMax;
    private Integer flairMin;
    private Integer flairMax;
    private Integer leadershipMin;
    private Integer leadershipMax;
    private Integer offTheBallMin;
    private Integer offTheBallMax;
    private Integer positioningMin;
    private Integer positioningMax;
    private Integer teamworkMin;
    private Integer teamworkMax;
    private Integer visionMin;
    private Integer visionMax;
    private Integer workRateMin;
    private Integer workRateMax;

    // 신체(Physical) 능력치 (Min, Max)
    private Integer accelerationMin;
    private Integer accelerationMax;
    private Integer agilityMin;
    private Integer agilityMax;
    private Integer balanceMin;
    private Integer balanceMax;
    private Integer jumpingReachMin;
    private Integer jumpingReachMax;
    private Integer naturalFitnessMin;
    private Integer naturalFitnessMax;
    private Integer paceMin;
    private Integer paceMax;
    private Integer staminaMin;
    private Integer staminaMax;
    private Integer strengthMin;
    private Integer strengthMax;
}
