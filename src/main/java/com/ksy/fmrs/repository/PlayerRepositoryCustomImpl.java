package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.QPlayer;
import com.ksy.fmrs.domain.enums.PositionEnum;
import com.ksy.fmrs.dto.SearchPlayerCondition;
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
    public List<Player> searchPlayerByName(String name) {
        return jpaQueryFactory
                .selectFrom(QPlayer.player)
                .where(nameContains(name))
                .fetch();
    }

    @Override
    public List<Player> searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        return jpaQueryFactory
                .selectFrom(QPlayer.player)
                .where(
                        nameContains(condition.getName()),
                        age(condition.getAgeMin(), condition.getAgeMax()),
                        position(condition.getPositionEnum()),
                        // technical
                        cornersMin(condition.getCorners()),
                        crossingMin(condition.getCrossing()),
                        dribblingMin(condition.getDribbling()),
                        finishingMin(condition.getFinishing()),
                        firstTouchMin(condition.getFirstTouch()),
                        freeKickTakingMin(condition.getFreeKickTaking()),
                        headingMin(condition.getHeading()),
                        longShotsMin(condition.getLongShots()),
                        longThrowsMin(condition.getLongThrows()),
                        markingMin(condition.getMarking()),
                        passingMin(condition.getPassing()),
                        penaltyTakingMin(condition.getPenaltyTaking()),
                        tacklingMin(condition.getTackling()),
                        techniqueMin(condition.getTechnique()),
                        // mental
                        aggressionMin(condition.getAggression()),
                        anticipationMin(condition.getAnticipation()),
                        braveryMin(condition.getBravery()),
                        composureMin(condition.getComposure()),
                        concentrationMin(condition.getConcentration()),
                        decisionsMin(condition.getDecisions()),
                        determinationMin(condition.getDetermination()),
                        flairMin(condition.getFlair()),
                        leadershipMin(condition.getLeadership()),
                        offTheBallMin(condition.getOffTheBall()),
                        positioningMin(condition.getPositioning()),
                        teamworkMin(condition.getTeamwork()),
                        visionMin(condition.getVision()),
                        workRateMin(condition.getWorkRate()),
                        // physical
                        accelerationMin(condition.getAcceleration()),
                        agilityMin(condition.getAgility()),
                        balanceMin(condition.getBalance()),
                        jumpingReachMin(condition.getJumpingReach()),
                        naturalFitnessMin(condition.getNaturalFitness()),
                        paceMin(condition.getPace()),
                        staminaMin(condition.getStamina()),
                        strengthMin(condition.getStrength())
                )
                .fetch();
    }

    // 검색 조건
    private BooleanExpression nameContains(String name){
        if(name == null || name.isEmpty()){
            return null;
        }
        return QPlayer.player.name.contains(name);
    }

    // 나이 초기값 14~99
    private BooleanExpression age(Integer ageMin, Integer ageMax){
        if(ageMin == null || ageMax == null || ageMin > ageMax){
            return null;
        }
        return QPlayer.player.age.between(ageMin, ageMax);
    }

    // 멀티포지션인 경우 고려해서 수정 필요
    private BooleanExpression position(PositionEnum positionEnum){
        if(positionEnum == null){
            return null;
        }
        return QPlayer.player.position.eq(positionEnum);
    }

    // 능력치 조건

    // crossing

    private BooleanExpression cornersMin(Integer corners) {
        if (corners == null) {
            return null;
        }
        return QPlayer.player.corners.goe(corners);
    }


    private BooleanExpression crossingMin(Integer crossingMin) {
        if (crossingMin == null) {
            return null;
        }
        return QPlayer.player.crossing.goe(crossingMin);
    }

    // dribbling
    private BooleanExpression dribblingMin(Integer dribblingMin) {
        if (dribblingMin == null) {
            return null;
        }
        return QPlayer.player.dribbling.goe(dribblingMin);
    }

    // finishing
    private BooleanExpression finishingMin(Integer finishingMin) {
        if (finishingMin == null) {
            return null;
        }
        return QPlayer.player.finishing.goe(finishingMin);
    }

    // firstTouch
    private BooleanExpression firstTouchMin(Integer firstTouchMin) {
        if (firstTouchMin == null) {
            return null;
        }
        return QPlayer.player.firstTouch.goe(firstTouchMin);
    }

    // freeKickTaking
    private BooleanExpression freeKickTakingMin(Integer freeKickTakingMin) {
        if (freeKickTakingMin == null) {
            return null;
        }
        return QPlayer.player.freeKickTaking.goe(freeKickTakingMin);
    }

    // heading
    private BooleanExpression headingMin(Integer headingMin) {
        if (headingMin == null) {
            return null;
        }
        return QPlayer.player.heading.goe(headingMin);
    }

    // longShots
    private BooleanExpression longShotsMin(Integer longShotsMin) {
        if (longShotsMin == null) {
            return null;
        }
        return QPlayer.player.longShots.goe(longShotsMin);
    }

    // longThrows
    private BooleanExpression longThrowsMin(Integer longThrowsMin) {
        if (longThrowsMin == null) {
            return null;
        }
        return QPlayer.player.longThrows.goe(longThrowsMin);
    }

    // marking
    private BooleanExpression markingMin(Integer markingMin) {
        if (markingMin == null) {
            return null;
        }
        return QPlayer.player.marking.goe(markingMin);
    }

    // passing
    private BooleanExpression passingMin(Integer passingMin) {
        if (passingMin == null) {
            return null;
        }
        return QPlayer.player.passing.goe(passingMin);
    }

    // penaltyTaking
    private BooleanExpression penaltyTakingMin(Integer penaltyTakingMin) {
        if (penaltyTakingMin == null) {
            return null;
        }
        return QPlayer.player.penaltyTaking.goe(penaltyTakingMin);
    }

    // tackling
    private BooleanExpression tacklingMin(Integer tacklingMin) {
        if (tacklingMin == null) {
            return null;
        }
        return QPlayer.player.tackling.goe(tacklingMin);
    }

    // technique
    private BooleanExpression techniqueMin(Integer techniqueMin) {
        if (techniqueMin == null) {
            return null;
        }
        return QPlayer.player.technique.goe(techniqueMin);
    }

    // aggression
    private BooleanExpression aggressionMin(Integer aggressionMin) {
        if (aggressionMin == null) {
            return null;
        }
        return QPlayer.player.aggression.goe(aggressionMin);
    }

    // anticipation
    private BooleanExpression anticipationMin(Integer anticipationMin) {
        if (anticipationMin == null) {
            return null;
        }
        return QPlayer.player.anticipation.goe(anticipationMin);
    }

    // bravery
    private BooleanExpression braveryMin(Integer braveryMin) {
        if (braveryMin == null) {
            return null;
        }
        return QPlayer.player.bravery.goe(braveryMin);
    }

    // composure
    private BooleanExpression composureMin(Integer composureMin) {
        if (composureMin == null) {
            return null;
        }
        return QPlayer.player.composure.goe(composureMin);
    }

    // concentration
    private BooleanExpression concentrationMin(Integer concentrationMin) {
        if (concentrationMin == null) {
            return null;
        }
        return QPlayer.player.concentration.goe(concentrationMin);
    }

    // decisions
    private BooleanExpression decisionsMin(Integer decisionsMin) {
        if (decisionsMin == null) {
            return null;
        }
        return QPlayer.player.decisions.goe(decisionsMin);
    }

    // determination
    private BooleanExpression determinationMin(Integer determinationMin) {
        if (determinationMin == null) {
            return null;
        }
        return QPlayer.player.determination.goe(determinationMin);
    }

    // flair
    private BooleanExpression flairMin(Integer flairMin) {
        if (flairMin == null) {
            return null;
        }
        return QPlayer.player.flair.goe(flairMin);
    }

    // leadership
    private BooleanExpression leadershipMin(Integer leadershipMin) {
        if (leadershipMin == null) {
            return null;
        }
        return QPlayer.player.leadership.goe(leadershipMin);
    }

    // offTheBall
    private BooleanExpression offTheBallMin(Integer offTheBallMin) {
        if (offTheBallMin == null) {
            return null;
        }
        return QPlayer.player.offTheBall.goe(offTheBallMin);
    }

    // positioning
    private BooleanExpression positioningMin(Integer positioningMin) {
        if (positioningMin == null) {
            return null;
        }
        return QPlayer.player.positioning.goe(positioningMin);
    }

    // teamwork
    private BooleanExpression teamworkMin(Integer teamworkMin) {
        if (teamworkMin == null) {
            return null;
        }
        return QPlayer.player.teamwork.goe(teamworkMin);
    }

    // vision
    private BooleanExpression visionMin(Integer visionMin) {
        if (visionMin == null) {
            return null;
        }
        return QPlayer.player.vision.goe(visionMin);
    }

    // workRate
    private BooleanExpression workRateMin(Integer workRateMin) {
        if (workRateMin == null) {
            return null;
        }
        return QPlayer.player.workRate.goe(workRateMin);
    }

    // acceleration
    private BooleanExpression accelerationMin(Integer accelerationMin) {
        if (accelerationMin == null) {
            return null;
        }
        return QPlayer.player.acceleration.goe(accelerationMin);
    }

    // agility
    private BooleanExpression agilityMin(Integer agilityMin) {
        if (agilityMin == null) {
            return null;
        }
        return QPlayer.player.agility.goe(agilityMin);
    }

    // balance
    private BooleanExpression balanceMin(Integer balanceMin) {
        if (balanceMin == null) {
            return null;
        }
        return QPlayer.player.balance.goe(balanceMin);
    }

    // jumpingReach
    private BooleanExpression jumpingReachMin(Integer jumpingReachMin) {
        if (jumpingReachMin == null) {
            return null;
        }
        return QPlayer.player.jumpingReach.goe(jumpingReachMin);
    }

    // naturalFitness
    private BooleanExpression naturalFitnessMin(Integer naturalFitnessMin) {
        if (naturalFitnessMin == null) {
            return null;
        }
        return QPlayer.player.naturalFitness.goe(naturalFitnessMin);
    }

    // pace
    private BooleanExpression paceMin(Integer paceMin) {
        if (paceMin == null) {
            return null;
        }
        return QPlayer.player.pace.goe(paceMin);
    }

    // stamina
    private BooleanExpression staminaMin(Integer staminaMin) {
        if (staminaMin == null) {
            return null;
        }
        return QPlayer.player.stamina.goe(staminaMin);
    }

    // strength
    private BooleanExpression strengthMin(Integer strengthMin) {
        if (strengthMin == null) {
            return null;
        }
        return QPlayer.player.strength.goe(strengthMin);
    }
}
