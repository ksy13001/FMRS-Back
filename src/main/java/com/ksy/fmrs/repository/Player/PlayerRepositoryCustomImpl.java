package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.QTeam;
import com.ksy.fmrs.domain.player.QPlayer;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.ksy.fmrs.domain.player.QFmPlayer.fmPlayer;
import static com.ksy.fmrs.domain.player.QPlayer.player;

@RequiredArgsConstructor
@Repository
public class PlayerRepositoryCustomImpl implements PlayerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // fmPlayer 에 대응되는 Player 가 하나 이상인 경우 mapping_state = FAILED 처리
    // 서브쿼리는 JPAExpressions 로 구현, 괄호랑 같은 역할
    public Long updateDuplicatedUnmappedPlayersToFailed() {
        return jpaQueryFactory
                .update(player)
                .set(player.mappingStatus, MappingStatus.FAILED)
                .where(
                        player.mappingStatus.eq(MappingStatus.UNMAPPED),
                        JPAExpressions
                                .select(fmPlayer.count())
                                .from(fmPlayer)
                                .where(
                                        fmPlayer.firstName.eq(player.firstName),
                                        fmPlayer.lastName.eq(player.lastName),
                                        fmPlayer.birth.eq(player.birth),
                                        fmPlayer.nationName.eq(player.nationName)
                                ).gt(1L)
                )
                .execute();
    }

    public Long updateDuplicatedUnmappedFMPlayersToFailed() {
        return jpaQueryFactory
                .update(player)
                .set(player.mappingStatus, MappingStatus.FAILED)
                .where(
                        player.mappingStatus.eq(MappingStatus.UNMAPPED),
                        JPAExpressions
                                .select(player.count())
                                .from(player)
                                .where(
                                        player.firstName.eq(fmPlayer.firstName),
                                        player.lastName.eq(fmPlayer.lastName),
                                        player.birth.eq(fmPlayer.birth),
                                        player.nationName.eq(fmPlayer.nationName)
                                ).gt(1L)
                )
                .execute();
    }

    public List<Player> findDuplicatedPlayers() {
        QPlayer p = QPlayer.player;

        // 1) 중복 키 추출
        List<Tuple> keys = jpaQueryFactory
                .select(p.firstName, p.lastName, p.birth, p.nationName)
                .from(p)
                .where(p.mappingStatus.eq(MappingStatus.UNMAPPED))
                .groupBy(p.firstName, p.lastName, p.birth, p.nationName)
                .having(p.count().gt(1))
                .fetch();

        if (keys.isEmpty()) return Collections.emptyList();

        // 2) OR 조건 조합 (≈ 300개면 부담 없음)
        BooleanBuilder cond = new BooleanBuilder();
        keys.forEach(k -> cond.or(
                p.firstName.eq(k.get(p.firstName))
                        .and(p.lastName.eq(k.get(p.lastName)))
                        .and(p.birth.eq(k.get(p.birth)))
                        .and(p.nationName.eq(k.get(p.nationName)))
        ));

        return jpaQueryFactory
                .selectFrom(p)
                .where(p.mappingStatus.eq(MappingStatus.UNMAPPED)
                        .and(cond))
                .fetch();
    }

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
                .selectFrom(player)
                .where(eqLastName(lastName), eqBirth(birth), eqFirstName(firstName), eqNationName(nation))
                .limit(1)
                .fetch();
    }

    // 이름 검색
    @Override
    public List<Player> searchPlayerByName(String name) {
        return jpaQueryFactory
                .selectFrom(player)
//                .where(nameContains(name))
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
//                .where(
//                        age(condition.getAgeMin(), condition.getAgeMax()),
//                        position(condition.getPositionEnum()),
//                        team(team, condition.getTeamName()),
//                        // technical
//                        cornersMin(condition.getCorners()),
//                        crossingMin(condition.getCrossing()),
//                        dribblingMin(condition.getDribbling()),
//                        finishingMin(condition.getFinishing()),
//                        firstTouchMin(condition.getFirstTouch()),
//                        freeKickTakingMin(condition.getFreeKickTaking()),
//                        headingMin(condition.getHeading()),
//                        longShotsMin(condition.getLongShots()),
//                        longThrowsMin(condition.getLongThrows()),
//                        markingMin(condition.getMarking()),
//                        passingMin(condition.getPassing()),
//                        penaltyTakingMin(condition.getPenaltyTaking()),
//                        tacklingMin(condition.getTackling()),
//                        techniqueMin(condition.getTechnique()),
//                        // mental
//                        aggressionMin(condition.getAggression()),
//                        anticipationMin(condition.getAnticipation()),
//                        braveryMin(condition.getBravery()),
//                        composureMin(condition.getComposure()),
//                        concentrationMin(condition.getConcentration()),
//                        decisionsMin(condition.getDecisions()),
//                        determinationMin(condition.getDetermination()),
//                        flairMin(condition.getFlair()),
//                        leadershipMin(condition.getLeadership()),
//                        offTheBallMin(condition.getOffTheBall()),
//                        positioningMin(condition.getPositioning()),
//                        teamworkMin(condition.getTeamwork()),
//                        visionMin(condition.getVision()),
//                        workRateMin(condition.getWorkRate()),
//                        // physical
//                        accelerationMin(condition.getAcceleration()),
//                        agilityMin(condition.getAgility()),
//                        balanceMin(condition.getBalance()),
//                        jumpingReachMin(condition.getJumpingReach()),
//                        naturalFitnessMin(condition.getNaturalFitness()),
//                        paceMin(condition.getPace()),
//                        staminaMin(condition.getStamina()),
//                        strengthMin(condition.getStrength())
//                )
                .fetch();
    }

    // 검색 조건
//    private BooleanExpression nameContains(String name) {
//        if (name == null || name.isEmpty()) {
//            return null;
//        }
//        return QPlayer.player.name.contains(name);
//    }

    private BooleanExpression eqFirstName(String first) {
        if (first == null || first.isEmpty()) {
            return null;
        }
        return player.firstName.eq(first);
    }

    private BooleanExpression eqNationName(String nation) {
        if (nation == null || nation.isEmpty()) {
            return null;
        }
        return  player.nationName.eq(nation);
    }

    private BooleanExpression eqBirth(LocalDate birth) {
        if (birth == null) {
            return null;
        }
        return player.birth.eq(birth);
    }


    private BooleanExpression eqLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return null;
        }
        return player.lastName.eq(lastName);
    }

//    private BooleanExpression eqAge(Integer age) {
//        if (age == null) {
//            return null;
//        }
//        return QPlayer.player.age.eq(age);
//    }


//    // 나이(초기값 14~99)
//    private BooleanExpression age(Integer ageMin, Integer ageMax) {
//        if (ageMin == null || ageMax == null || ageMin > ageMax) {
//            return null;
//        }
//        return QPlayer.player.age.between(ageMin, ageMax);
//    }

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
//
//    private BooleanExpression cornersMin(Integer corners) {
//        if (corners == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.corners.goe(corners);
//    }
//
//
//    private BooleanExpression crossingMin(Integer crossingMin) {
//        if (crossingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.crossing.goe(crossingMin);
//    }
//
//    // dribbling
//    private BooleanExpression dribblingMin(Integer dribblingMin) {
//        if (dribblingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.dribbling.goe(dribblingMin);
//    }
//
//    // finishing
//    private BooleanExpression finishingMin(Integer finishingMin) {
//        if (finishingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.finishing.goe(finishingMin);
//    }
//
//    // firstTouch
//    private BooleanExpression firstTouchMin(Integer firstTouchMin) {
//        if (firstTouchMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.firstTouch.goe(firstTouchMin);
//    }
//
//    // freeKickTaking
//    private BooleanExpression freeKickTakingMin(Integer freeKickTakingMin) {
//        if (freeKickTakingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.freeKincks.goe(freeKickTakingMin);
//    }
//
//    // heading
//    private BooleanExpression headingMin(Integer headingMin) {
//        if (headingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.heading.goe(headingMin);
//    }
//
//    // longShots
//    private BooleanExpression longShotsMin(Integer longShotsMin) {
//        if (longShotsMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.longShots.goe(longShotsMin);
//    }
//
//    // longThrows
//    private BooleanExpression longThrowsMin(Integer longThrowsMin) {
//        if (longThrowsMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.longThrows.goe(longThrowsMin);
//    }
//
//    // marking
//    private BooleanExpression markingMin(Integer markingMin) {
//        if (markingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.marking.goe(markingMin);
//    }
//
//    // passing
//    private BooleanExpression passingMin(Integer passingMin) {
//        if (passingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.passing.goe(passingMin);
//    }
//
//    // penaltyTaking
//    private BooleanExpression penaltyTakingMin(Integer penaltyTakingMin) {
//        if (penaltyTakingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.penaltyTaking.goe(penaltyTakingMin);
//    }
//
//    // tackling
//    private BooleanExpression tacklingMin(Integer tacklingMin) {
//        if (tacklingMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.tackling.goe(tacklingMin);
//    }
//
//    // technique
//    private BooleanExpression techniqueMin(Integer techniqueMin) {
//        if (techniqueMin == null) {
//            return null;
//        }
//        return QPlayer.player.technicalAttributes.technique.goe(techniqueMin);
//    }
//
//    // aggression
//    private BooleanExpression aggressionMin(Integer aggressionMin) {
//        if (aggressionMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.aggression.goe(aggressionMin);
//    }
//
//    // anticipation
//    private BooleanExpression anticipationMin(Integer anticipationMin) {
//        if (anticipationMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.anticipation.goe(anticipationMin);
//    }
//
//    // bravery
//    private BooleanExpression braveryMin(Integer braveryMin) {
//        if (braveryMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.bravery.goe(braveryMin);
//    }
//
//    // composure
//    private BooleanExpression composureMin(Integer composureMin) {
//        if (composureMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.composure.goe(composureMin);
//    }
//
//    // concentration
//    private BooleanExpression concentrationMin(Integer concentrationMin) {
//        if (concentrationMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.concentration.goe(concentrationMin);
//    }
//
//    // decisions
//    private BooleanExpression decisionsMin(Integer decisionsMin) {
//        if (decisionsMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.decisions.goe(decisionsMin);
//    }
//
//    // determination
//    private BooleanExpression determinationMin(Integer determinationMin) {
//        if (determinationMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.determination.goe(determinationMin);
//    }
//
//    // flair
//    private BooleanExpression flairMin(Integer flairMin) {
//        if (flairMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.flair.goe(flairMin);
//    }
//
//    // leadership
//    private BooleanExpression leadershipMin(Integer leadershipMin) {
//        if (leadershipMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.leadership.goe(leadershipMin);
//    }
//
//    // offTheBall
//    private BooleanExpression offTheBallMin(Integer offTheBallMin) {
//        if (offTheBallMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.offTheBall.goe(offTheBallMin);
//    }
//
//    // positioning
//    private BooleanExpression positioningMin(Integer positioningMin) {
//        if (positioningMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.positioning.goe(positioningMin);
//    }
//
//    // teamwork
//    private BooleanExpression teamworkMin(Integer teamworkMin) {
//        if (teamworkMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.teamwork.goe(teamworkMin);
//    }
//
//    // vision
//    private BooleanExpression visionMin(Integer visionMin) {
//        if (visionMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.vision.goe(visionMin);
//    }
//
//    // workRate
//    private BooleanExpression workRateMin(Integer workRateMin) {
//        if (workRateMin == null) {
//            return null;
//        }
//        return QPlayer.player.mentalAttributes.workRate.goe(workRateMin);
//    }
//
//    // acceleration
//    private BooleanExpression accelerationMin(Integer accelerationMin) {
//        if (accelerationMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.acceleration.goe(accelerationMin);
//    }
//
//    // agility
//    private BooleanExpression agilityMin(Integer agilityMin) {
//        if (agilityMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.agility.goe(agilityMin);
//    }
//
//    // balance
//    private BooleanExpression balanceMin(Integer balanceMin) {
//        if (balanceMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.balance.goe(balanceMin);
//    }
//
//    // jumpingReach
//    private BooleanExpression jumpingReachMin(Integer jumpingReachMin) {
//        if (jumpingReachMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.jumpingReach.goe(jumpingReachMin);
//    }
//
//    // naturalFitness
//    private BooleanExpression naturalFitnessMin(Integer naturalFitnessMin) {
//        if (naturalFitnessMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.naturalFitness.goe(naturalFitnessMin);
//    }
//
//    // pace
//    private BooleanExpression paceMin(Integer paceMin) {
//        if (paceMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.pace.goe(paceMin);
//    }
//
//    // stamina
//    private BooleanExpression staminaMin(Integer staminaMin) {
//        if (staminaMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.stamina.goe(staminaMin);
//    }
//
//    // strength
//    private BooleanExpression strengthMin(Integer strengthMin) {
//        if (strengthMin == null) {
//            return null;
//        }
//        return QPlayer.player.physicalAttributes.strength.goe(strengthMin);
//    }
}
