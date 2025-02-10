package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.QPlayer;
import com.ksy.fmrs.domain.enums.Position;
import com.ksy.fmrs.dto.PlayerSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlayerRepositoryCustomImpl implements PlayerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Player> searchPlayer(PlayerSearchCondition condition) {
        return jpaQueryFactory
                .selectFrom(QPlayer.player)
                .where(
                        nameContains(condition.getName()),
                        age(condition.getAgeMin(), condition.getAgeMax()),
                        position(condition.getPosition()),
                        //technical
                        corners(condition.getCornersMin(), condition.getCornersMax()),
                        crossing(condition.getCrossingMin(), condition.getCrossingMax()),
                        dribbling(condition.getDribblingMin(), condition.getDribblingMax()),
                        finishing(condition.getFinishingMin(), condition.getFinishingMax()),
                        firstTouch(condition.getFirstTouchMin(), condition.getFirstTouchMax()),
                        freeKickTaking(condition.getFreeKickTakingMin(), condition.getFreeKickTakingMax()),
                        heading(condition.getHeadingMin(), condition.getHeadingMax()),
                        longShots(condition.getLongShotsMin(), condition.getLongShotsMax()),
                        longThrows(condition.getLongThrowsMin(), condition.getLongThrowsMax()),
                        marking(condition.getMarkingMin(), condition.getMarkingMax()),
                        passing(condition.getPassingMin(), condition.getPassingMax()),
                        penaltyTaking(condition.getPenaltyTakingMin(), condition.getPenaltyTakingMax()),
                        tackling(condition.getTacklingMin(), condition.getTacklingMax()),
                        technique(condition.getTechniqueMin(), condition.getTechniqueMax()),
                        //mental
                        aggression(condition.getAggressionMin(), condition.getAggressionMax()),
                        anticipation(condition.getAnticipationMin(), condition.getAnticipationMax()),
                        bravery(condition.getBraveryMin(), condition.getBraveryMax()),
                        composure(condition.getComposureMin(), condition.getComposureMax()),
                        concentration(condition.getConcentrationMin(), condition.getConcentrationMax()),
                        decisions(condition.getDecisionsMin(), condition.getDecisionsMax()),
                        determination(condition.getDeterminationMin(), condition.getDeterminationMax()),
                        flair(condition.getFlairMin(), condition.getFlairMax()),
                        leadership(condition.getLeadershipMin(), condition.getLeadershipMax()),
                        offTheBall(condition.getOffTheBallMin(), condition.getOffTheBallMax()),
                        positioning(condition.getPositioningMin(), condition.getPositioningMax()),
                        teamwork(condition.getTeamworkMin(), condition.getTeamworkMax()),
                        vision(condition.getVisionMin(), condition.getVisionMax()),
                        workRate(condition.getWorkRateMin(), condition.getWorkRateMax()),
                        //Physical
                        acceleration(condition.getAccelerationMin(), condition.getAccelerationMax()),
                        agility(condition.getAgilityMin(), condition.getAgilityMax()),
                        balance(condition.getBalanceMin(), condition.getBalanceMax()),
                        jumpingReach(condition.getJumpingReachMin(), condition.getJumpingReachMax()),
                        naturalFitness(condition.getNaturalFitnessMin(), condition.getNaturalFitnessMax()),
                        pace(condition.getPaceMin(), condition.getPaceMax()),
                        stamina(condition.getStaminaMin(), condition.getStaminaMax()),
                        strength(condition.getStrengthMin(), condition.getStrengthMax())
                )
                .fetch();
    }

    // 검색 조건
    private BooleanExpression nameContains(String name){
        if(name == null || name.isEmpty()){
            return null;
        }
        return QPlayer.player.name.containsIgnoreCase(name);
    }

    // 나이 초기값 14~99
    private BooleanExpression age(Integer ageMin, Integer ageMax){
        if(ageMin == null || ageMax == null || ageMin > ageMax){
            return null;
        }
        return QPlayer.player.age.between(ageMin, ageMax);
    }

    // 멀티포지션인 경우 고려해서 수정 필요
    private BooleanExpression position(Position position){
        if(position == null){
            return null;
        }
        return QPlayer.player.position.eq(position);
    }

    private BooleanExpression corners(Integer cornersMin, Integer cornersMax) {
        if (cornersMin == null || cornersMax == null || cornersMin > cornersMax) {
            return null;
        }
        return QPlayer.player.corners.between(cornersMin, cornersMax);
    }

    private BooleanExpression crossing(Integer crossingMin, Integer crossingMax) {
        if (crossingMin == null || crossingMax == null || crossingMin > crossingMax) {
            return null;
        }
        return QPlayer.player.crossing.between(crossingMin, crossingMax);
    }

    private BooleanExpression dribbling(Integer dribblingMin, Integer dribblingMax) {
        if (dribblingMin == null || dribblingMax == null || dribblingMin > dribblingMax) {
            return null;
        }
        return QPlayer.player.dribbling.between(dribblingMin, dribblingMax);
    }

    private BooleanExpression finishing(Integer finishingMin, Integer finishingMax) {
        if (finishingMin == null || finishingMax == null || finishingMin > finishingMax) {
            return null;
        }
        return QPlayer.player.finishing.between(finishingMin, finishingMax);
    }

    private BooleanExpression firstTouch(Integer firstTouchMin, Integer firstTouchMax) {
        if (firstTouchMin == null || firstTouchMax == null || firstTouchMin > firstTouchMax) {
            return null;
        }
        return QPlayer.player.firstTouch.between(firstTouchMin, firstTouchMax);
    }

    private BooleanExpression freeKickTaking(Integer freeKickTakingMin, Integer freeKickTakingMax) {
        if (freeKickTakingMin == null || freeKickTakingMax == null || freeKickTakingMin > freeKickTakingMax) {
            return null;
        }
        return QPlayer.player.freeKickTaking.between(freeKickTakingMin, freeKickTakingMax);
    }

    private BooleanExpression heading(Integer headingMin, Integer headingMax) {
        if (headingMin == null || headingMax == null || headingMin > headingMax) {
            return null;
        }
        return QPlayer.player.heading.between(headingMin, headingMax);
    }

    private BooleanExpression longShots(Integer longShotsMin, Integer longShotsMax) {
        if (longShotsMin == null || longShotsMax == null || longShotsMin > longShotsMax) {
            return null;
        }
        return QPlayer.player.longShots.between(longShotsMin, longShotsMax);
    }

    private BooleanExpression longThrows(Integer longThrowsMin, Integer longThrowsMax) {
        if (longThrowsMin == null || longThrowsMax == null || longThrowsMin > longThrowsMax) {
            return null;
        }
        return QPlayer.player.longThrows.between(longThrowsMin, longThrowsMax);
    }

    private BooleanExpression marking(Integer markingMin, Integer markingMax) {
        if (markingMin == null || markingMax == null || markingMin > markingMax) {
            return null;
        }
        return QPlayer.player.marking.between(markingMin, markingMax);
    }

    private BooleanExpression passing(Integer passingMin, Integer passingMax) {
        if (passingMin == null || passingMax == null || passingMin > passingMax) {
            return null;
        }
        return QPlayer.player.passing.between(passingMin, passingMax);
    }

    private BooleanExpression penaltyTaking(Integer penaltyTakingMin, Integer penaltyTakingMax) {
        if (penaltyTakingMin == null || penaltyTakingMax == null || penaltyTakingMin > penaltyTakingMax) {
            return null;
        }
        return QPlayer.player.penaltyTaking.between(penaltyTakingMin, penaltyTakingMax);
    }

    private BooleanExpression tackling(Integer tacklingMin, Integer tacklingMax) {
        if (tacklingMin == null || tacklingMax == null || tacklingMin > tacklingMax) {
            return null;
        }
        return QPlayer.player.tackling.between(tacklingMin, tacklingMax);
    }

    private BooleanExpression technique(Integer techniqueMin, Integer techniqueMax) {
        if (techniqueMin == null || techniqueMax == null || techniqueMin > techniqueMax) {
            return null;
        }
        return QPlayer.player.technique.between(techniqueMin, techniqueMax);
    }

    private BooleanExpression aggression(Integer aggressionMin, Integer aggressionMax) {
        if (aggressionMin == null || aggressionMax == null || aggressionMin > aggressionMax) {
            return null;
        }
        return QPlayer.player.aggression.between(aggressionMin, aggressionMax);
    }

    private BooleanExpression anticipation(Integer anticipationMin, Integer anticipationMax) {
        if (anticipationMin == null || anticipationMax == null || anticipationMin > anticipationMax) {
            return null;
        }
        return QPlayer.player.anticipation.between(anticipationMin, anticipationMax);
    }

    private BooleanExpression bravery(Integer braveryMin, Integer braveryMax) {
        if (braveryMin == null || braveryMax == null || braveryMin > braveryMax) {
            return null;
        }
        return QPlayer.player.bravery.between(braveryMin, braveryMax);
    }

    private BooleanExpression composure(Integer composureMin, Integer composureMax) {
        if (composureMin == null || composureMax == null || composureMin > composureMax) {
            return null;
        }
        return QPlayer.player.composure.between(composureMin, composureMax);
    }

    private BooleanExpression concentration(Integer concentrationMin, Integer concentrationMax) {
        if (concentrationMin == null || concentrationMax == null || concentrationMin > concentrationMax) {
            return null;
        }
        return QPlayer.player.concentration.between(concentrationMin, concentrationMax);
    }

    private BooleanExpression decisions(Integer decisionsMin, Integer decisionsMax) {
        if (decisionsMin == null || decisionsMax == null || decisionsMin > decisionsMax) {
            return null;
        }
        return QPlayer.player.decisions.between(decisionsMin, decisionsMax);
    }

    private BooleanExpression determination(Integer determinationMin, Integer determinationMax) {
        if (determinationMin == null || determinationMax == null || determinationMin > determinationMax) {
            return null;
        }
        return QPlayer.player.determination.between(determinationMin, determinationMax);
    }

    private BooleanExpression flair(Integer flairMin, Integer flairMax) {
        if (flairMin == null || flairMax == null || flairMin > flairMax) {
            return null;
        }
        return QPlayer.player.flair.between(flairMin, flairMax);
    }

    private BooleanExpression leadership(Integer leadershipMin, Integer leadershipMax) {
        if (leadershipMin == null || leadershipMax == null || leadershipMin > leadershipMax) {
            return null;
        }
        return QPlayer.player.leadership.between(leadershipMin, leadershipMax);
    }

    private BooleanExpression offTheBall(Integer offTheBallMin, Integer offTheBallMax) {
        if (offTheBallMin == null || offTheBallMax == null || offTheBallMin > offTheBallMax) {
            return null;
        }
        return QPlayer.player.offTheBall.between(offTheBallMin, offTheBallMax);
    }

    private BooleanExpression positioning(Integer positioningMin, Integer positioningMax) {
        if (positioningMin == null || positioningMax == null || positioningMin > positioningMax) {
            return null;
        }
        return QPlayer.player.positioning.between(positioningMin, positioningMax);
    }

    private BooleanExpression teamwork(Integer teamworkMin, Integer teamworkMax) {
        if (teamworkMin == null || teamworkMax == null || teamworkMin > teamworkMax) {
            return null;
        }
        return QPlayer.player.teamwork.between(teamworkMin, teamworkMax);
    }

    private BooleanExpression vision(Integer visionMin, Integer visionMax) {
        if (visionMin == null || visionMax == null || visionMin > visionMax) {
            return null;
        }
        return QPlayer.player.vision.between(visionMin, visionMax);
    }

    private BooleanExpression workRate(Integer workRateMin, Integer workRateMax) {
        if (workRateMin == null || workRateMax == null || workRateMin > workRateMax) {
            return null;
        }
        return QPlayer.player.workRate.between(workRateMin, workRateMax);
    }

    private BooleanExpression acceleration(Integer accelerationMin, Integer accelerationMax) {
        if (accelerationMin == null || accelerationMax == null || accelerationMin > accelerationMax) {
            return null;
        }
        return QPlayer.player.acceleration.between(accelerationMin, accelerationMax);
    }

    private BooleanExpression agility(Integer agilityMin, Integer agilityMax) {
        if (agilityMin == null || agilityMax == null || agilityMin > agilityMax) {
            return null;
        }
        return QPlayer.player.agility.between(agilityMin, agilityMax);
    }

    private BooleanExpression balance(Integer balanceMin, Integer balanceMax) {
        if (balanceMin == null || balanceMax == null || balanceMin > balanceMax) {
            return null;
        }
        return QPlayer.player.balance.between(balanceMin, balanceMax);
    }

    private BooleanExpression jumpingReach(Integer jumpingReachMin, Integer jumpingReachMax) {
        if (jumpingReachMin == null || jumpingReachMax == null || jumpingReachMin > jumpingReachMax) {
            return null;
        }
        return QPlayer.player.jumpingReach.between(jumpingReachMin, jumpingReachMax);
    }

    private BooleanExpression naturalFitness(Integer naturalFitnessMin, Integer naturalFitnessMax) {
        if (naturalFitnessMin == null || naturalFitnessMax == null || naturalFitnessMin > naturalFitnessMax) {
            return null;
        }
        return QPlayer.player.naturalFitness.between(naturalFitnessMin, naturalFitnessMax);
    }

    private BooleanExpression pace(Integer paceMin, Integer paceMax) {
        if (paceMin == null || paceMax == null || paceMin > paceMax) {
            return null;
        }
        return QPlayer.player.pace.between(paceMin, paceMax);
    }

    private BooleanExpression stamina(Integer staminaMin, Integer staminaMax) {
        if (staminaMin == null || staminaMax == null || staminaMin > staminaMax) {
            return null;
        }
        return QPlayer.player.stamina.between(staminaMin, staminaMax);
    }

    private BooleanExpression strength(Integer strengthMin, Integer strengthMax) {
        if (strengthMin == null || strengthMax == null || strengthMin > strengthMax) {
            return null;
        }
        return QPlayer.player.strength.between(strengthMin, strengthMax);
    }



}
