package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.QTeam;
import com.ksy.fmrs.domain.player.QPlayer;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlayerRepositoryCustomImpl implements PlayerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

//    // 팀 소속 선수들 조회 기능
//    @Override
//    public List<Player> getPlayersByTeamId(Long teamId) {
//        return jpaQueryFactory.selectFrom(QPlayer.player)
//                .join(QTeam.team.players, QPlayer.player)
//                .where(QPlayer.player.team.id.eq(teamId))
//                .fetch();
//    }

    // firstName, lastName, 나이, 국가로 검색
    @Override
    public List<Player> searchPlayerByFm(String firstName, String lastName, LocalDate birth, String nation) {
        return jpaQueryFactory
                .selectFrom(QPlayer.player)
                .where(eqLastName(lastName), eqBirth(birth), eqFirstName(firstName), eqNationName(nation))
                .limit(1)
                .fetch();
    }

    // 이름 검색
    @Override
    public List<Player> searchPlayerByName(String name) {
        return jpaQueryFactory
                .selectFrom(QPlayer.player)
                .where(nameContains(name))
                .fetch();
    }

    // 나이, 포지션, 능력치, 나라, 팀
    @Override
    public List<Player> searchPlayerByDetailCondition(SearchPlayerCondition condition) {
        QPlayer player = QPlayer.player;
        QTeam team = QTeam.team;
        return jpaQueryFactory
                .selectFrom(player)
                .leftJoin(player.team, team)    // 무소속인 선수들까지 가져오기 위해 leftJoin
                .where(
                        age(condition.getAgeMin(), condition.getAgeMax()),
//                        position(condition.getPositionEnum()),
                        team(team, condition.getTeamName()),
//                        // technical
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
    private BooleanExpression nameContains(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return QPlayer.player.name.contains(name);
    }

    private BooleanExpression eqFirstName(String first) {
        if (first == null || first.isEmpty()) {
            return null;
        }
        return QPlayer.player.firstName.eq(first);
    }

    private BooleanExpression eqNationName(String nation) {
        if (nation == null || nation.isEmpty()) {
            return null;
        }
        return  QPlayer.player.nationName.eq(nation);
    }

    private BooleanExpression eqBirth(LocalDate birth) {
        if (birth == null) {
            return null;
        }
        return QPlayer.player.birth.eq(birth);
    }


    private BooleanExpression eqLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return null;
        }
        return QPlayer.player.lastName.eq(lastName);
    }

    private BooleanExpression eqAge(Integer age) {
        if (age == null) {
            return null;
        }
        return QPlayer.player.age.eq(age);
    }


    // 나이(초기값 14~99)
    private BooleanExpression age(Integer ageMin, Integer ageMax) {
        if (ageMin == null || ageMax == null || ageMin > ageMax) {
            return null;
        }
        return QPlayer.player.age.between(ageMin, ageMax);
    }

    // 포지션(멀티포지션인 경우 고려해서 수정 필요)
//    private BooleanExpression position(PositionEnum positionEnum){
//        if(positionEnum == null){
//            return null;
//        }
//        return QPlayer.player.position.eq(positionEnum);
//    }

    // 팀
    private BooleanExpression team(QTeam team, String teamName) {
        if (teamName == null || teamName.isEmpty()) {
            return null;
        }
        return team.name.eq(teamName);
    }

    // 능력치 조건

    // crossing

    private BooleanExpression cornersMin(Integer corners) {
        if (corners == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.corners.goe(corners);
    }


    private BooleanExpression crossingMin(Integer crossingMin) {
        if (crossingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.crossing.goe(crossingMin);
    }

    // dribbling
    private BooleanExpression dribblingMin(Integer dribblingMin) {
        if (dribblingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.dribbling.goe(dribblingMin);
    }

    // finishing
    private BooleanExpression finishingMin(Integer finishingMin) {
        if (finishingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.finishing.goe(finishingMin);
    }

    // firstTouch
    private BooleanExpression firstTouchMin(Integer firstTouchMin) {
        if (firstTouchMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.firstTouch.goe(firstTouchMin);
    }

    // freeKickTaking
    private BooleanExpression freeKickTakingMin(Integer freeKickTakingMin) {
        if (freeKickTakingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.freeKincks.goe(freeKickTakingMin);
    }

    // heading
    private BooleanExpression headingMin(Integer headingMin) {
        if (headingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.heading.goe(headingMin);
    }

    // longShots
    private BooleanExpression longShotsMin(Integer longShotsMin) {
        if (longShotsMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.longShots.goe(longShotsMin);
    }

    // longThrows
    private BooleanExpression longThrowsMin(Integer longThrowsMin) {
        if (longThrowsMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.longThrows.goe(longThrowsMin);
    }

    // marking
    private BooleanExpression markingMin(Integer markingMin) {
        if (markingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.marking.goe(markingMin);
    }

    // passing
    private BooleanExpression passingMin(Integer passingMin) {
        if (passingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.passing.goe(passingMin);
    }

    // penaltyTaking
    private BooleanExpression penaltyTakingMin(Integer penaltyTakingMin) {
        if (penaltyTakingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.penaltyTaking.goe(penaltyTakingMin);
    }

    // tackling
    private BooleanExpression tacklingMin(Integer tacklingMin) {
        if (tacklingMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.tackling.goe(tacklingMin);
    }

    // technique
    private BooleanExpression techniqueMin(Integer techniqueMin) {
        if (techniqueMin == null) {
            return null;
        }
        return QPlayer.player.technicalAttributes.technique.goe(techniqueMin);
    }

    // aggression
    private BooleanExpression aggressionMin(Integer aggressionMin) {
        if (aggressionMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.aggression.goe(aggressionMin);
    }

    // anticipation
    private BooleanExpression anticipationMin(Integer anticipationMin) {
        if (anticipationMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.anticipation.goe(anticipationMin);
    }

    // bravery
    private BooleanExpression braveryMin(Integer braveryMin) {
        if (braveryMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.bravery.goe(braveryMin);
    }

    // composure
    private BooleanExpression composureMin(Integer composureMin) {
        if (composureMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.composure.goe(composureMin);
    }

    // concentration
    private BooleanExpression concentrationMin(Integer concentrationMin) {
        if (concentrationMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.concentration.goe(concentrationMin);
    }

    // decisions
    private BooleanExpression decisionsMin(Integer decisionsMin) {
        if (decisionsMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.decisions.goe(decisionsMin);
    }

    // determination
    private BooleanExpression determinationMin(Integer determinationMin) {
        if (determinationMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.determination.goe(determinationMin);
    }

    // flair
    private BooleanExpression flairMin(Integer flairMin) {
        if (flairMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.flair.goe(flairMin);
    }

    // leadership
    private BooleanExpression leadershipMin(Integer leadershipMin) {
        if (leadershipMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.leadership.goe(leadershipMin);
    }

    // offTheBall
    private BooleanExpression offTheBallMin(Integer offTheBallMin) {
        if (offTheBallMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.offTheBall.goe(offTheBallMin);
    }

    // positioning
    private BooleanExpression positioningMin(Integer positioningMin) {
        if (positioningMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.positioning.goe(positioningMin);
    }

    // teamwork
    private BooleanExpression teamworkMin(Integer teamworkMin) {
        if (teamworkMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.teamwork.goe(teamworkMin);
    }

    // vision
    private BooleanExpression visionMin(Integer visionMin) {
        if (visionMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.vision.goe(visionMin);
    }

    // workRate
    private BooleanExpression workRateMin(Integer workRateMin) {
        if (workRateMin == null) {
            return null;
        }
        return QPlayer.player.mentalAttributes.workRate.goe(workRateMin);
    }

    // acceleration
    private BooleanExpression accelerationMin(Integer accelerationMin) {
        if (accelerationMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.acceleration.goe(accelerationMin);
    }

    // agility
    private BooleanExpression agilityMin(Integer agilityMin) {
        if (agilityMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.agility.goe(agilityMin);
    }

    // balance
    private BooleanExpression balanceMin(Integer balanceMin) {
        if (balanceMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.balance.goe(balanceMin);
    }

    // jumpingReach
    private BooleanExpression jumpingReachMin(Integer jumpingReachMin) {
        if (jumpingReachMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.jumpingReach.goe(jumpingReachMin);
    }

    // naturalFitness
    private BooleanExpression naturalFitnessMin(Integer naturalFitnessMin) {
        if (naturalFitnessMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.naturalFitness.goe(naturalFitnessMin);
    }

    // pace
    private BooleanExpression paceMin(Integer paceMin) {
        if (paceMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.pace.goe(paceMin);
    }

    // stamina
    private BooleanExpression staminaMin(Integer staminaMin) {
        if (staminaMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.stamina.goe(staminaMin);
    }

    // strength
    private BooleanExpression strengthMin(Integer strengthMin) {
        if (strengthMin == null) {
            return null;
        }
        return QPlayer.player.physicalAttributes.strength.goe(strengthMin);
    }
}
