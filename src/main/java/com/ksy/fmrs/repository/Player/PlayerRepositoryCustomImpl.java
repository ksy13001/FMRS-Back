package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.QPlayer;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import com.ksy.fmrs.util.time.TimeProvider;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.ksy.fmrs.domain.QLeague.league;
import static com.ksy.fmrs.domain.QTeam.team;
import static com.ksy.fmrs.domain.player.QFmPlayer.fmPlayer;
import static com.ksy.fmrs.domain.player.QPlayer.player;

@RequiredArgsConstructor
@Repository
public class PlayerRepositoryCustomImpl implements PlayerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final TimeProvider timeProvider;

    // firstName, lastName, 나이, 국가로 검색
    @Override
    public List<Player> searchPlayerByFm(String firstName, String lastName, LocalDate birth, String nation) {
        return jpaQueryFactory
                .selectFrom(player)
                .where(eqLastName(lastName), eqBirth(birth), eqFirstName(firstName), nationNameEq(nation))
                .limit(1)
                .fetch();
    }

    // 이름 검색
    @Override
    public Slice<Player> searchPlayerByName(
            String name, Pageable pageable, Long lastPlayerId, Integer lastCurrentAbility, MappingStatus lastmappingStatus) {

        int limit = pageable.getPageSize();
        List<Player> players = jpaQueryFactory
                .selectFrom(player)
                .leftJoin(player.fmPlayer, fmPlayer).fetchJoin()
                .where(firstNameStartWith(name).or(lastNameStartWith(name))
                        , mappingStatusAndIdCursorPredicate(lastmappingStatus, lastCurrentAbility, lastPlayerId))
                .orderBy(mappingStatusRankExpr().asc(), fmPlayer.currentAbility.desc(), player.id.asc())
                .limit(limit + 1) // limit + 1만큼 불러 와지면 다음 페이지가 존재함
                .fetch();

        boolean hasNext = players.size() > limit;
        List<Player> content = hasNext ? players.subList(0, limit) : players;
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Page<Player> searchPlayerByDetailCondition(SearchPlayerCondition condition, Pageable pageable) {
        List<Player> players = jpaQueryFactory
                .selectFrom(player)
                .leftJoin(player.team, team).fetchJoin()   // 무소속인 선수들까지 가져오기 위해 leftJoin
                .leftJoin(player.team.league, league).fetchJoin()
                .leftJoin(player.fmPlayer, fmPlayer).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .where(playerDetailSearchCondition(condition))
                .orderBy(fmPlayer.currentAbility.desc(), player.id.asc())
                .fetch();

        Long count = jpaQueryFactory
                .select(player.count())
                .from(player)
                .leftJoin(player.team, team)
                .leftJoin(player.team.league, league)
                .leftJoin(player.fmPlayer, fmPlayer)
                .where(playerDetailSearchCondition(condition))
                .fetchOne();

        return new PageImpl<>(players, pageable, count);
    }

    //     검색 조건
    private BooleanExpression nameContains(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return QPlayer.player.name.containsIgnoreCase(name);
    }

    private BooleanExpression firstNameStartWith(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return null;
        }
        return player.firstName.startsWithIgnoreCase(firstName);
    }

    private BooleanExpression lastNameStartWith(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return null;
        }
        return player.lastName.startsWithIgnoreCase(lastName);
    }

    /**
     * ORDER BY
     * 1. mappingStatus - MATCHED, UNMAPPED, FAILED
     * 2. if mappingStatus == MATCHED, currentAbility desc
     * 3. id asc
     */
    private BooleanExpression mappingStatusAndIdCursorPredicate(
            MappingStatus lastMappingStatus, Integer lastCurrentAbility, Long lastPlayerId) {
        if (lastPlayerId == null || lastMappingStatus == null) {
            return null;
        }
        int lastMappingStatusRank = switch (lastMappingStatus) {
            case MATCHED -> 0;
            case UNMAPPED -> 1;
            default -> 2;
        };

        if (lastMappingStatus != MappingStatus.MATCHED || lastCurrentAbility == null) {
            return mappingStatusRankExpr().gt(lastMappingStatusRank).or(
                    mappingStatusRankExpr().eq(lastMappingStatusRank).and(player.id.gt(lastPlayerId))
            );
        }
        return mappingStatusRankExpr().gt(lastMappingStatusRank).or(
                mappingStatusRankExpr().eq(lastMappingStatusRank).and(
                        fmPlayer.currentAbility.lt(lastCurrentAbility).or(
                                fmPlayer.currentAbility.eq(lastCurrentAbility).and(player.id.gt(lastPlayerId))
                        )
                )
        );
    }

    // MATCHED -> UNMAPPED -> FAILED 순으로 정렬
    private NumberExpression<Integer> mappingStatusRankExpr() {
        return new CaseBuilder()
                .when(player.mappingStatus.eq(MappingStatus.MATCHED)).then(0)
                .when(player.mappingStatus.eq(MappingStatus.UNMAPPED)).then(1)
                .otherwise(2);
    }

    private BooleanExpression eqFirstName(String first) {
        if (first == null || first.isEmpty()) {
            return null;
        }
        return player.firstName.eq(first);
    }

    private BooleanExpression nationNameEq(String nation) {
        if (nation == null || nation.isEmpty()) {
            return null;
        }
        return player.nationName.eq(nation);
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

    private BooleanExpression playerDetailSearchCondition(SearchPlayerCondition c) {
        if (c == null) {
            return null;
        }
        return Expressions.allOf(
                ageBetween(c.getAgeMin(), c.getAgeMax()),
                teamIdEq(c.getTeamId()),
                leagueIdEq(c.getLeagueId()),
                nationNameEq(c.getNationName()),

                // Technical
                goeStat(c.getCorners(), fmPlayer.technicalAttributes.corners),
                goeStat(c.getCrossing(), fmPlayer.technicalAttributes.crossing),
                goeStat(c.getDribbling(), fmPlayer.technicalAttributes.dribbling),
                goeStat(c.getFinishing(), fmPlayer.technicalAttributes.finishing),
                goeStat(c.getFirstTouch(), fmPlayer.technicalAttributes.firstTouch),
                goeStat(c.getFreeKickTaking(), fmPlayer.technicalAttributes.freeKicks),
                goeStat(c.getHeading(), fmPlayer.technicalAttributes.heading),
                goeStat(c.getLongShots(), fmPlayer.technicalAttributes.longShots),
                goeStat(c.getLongThrows(), fmPlayer.technicalAttributes.longThrows),
                goeStat(c.getMarking(), fmPlayer.technicalAttributes.marking),
                goeStat(c.getPassing(), fmPlayer.technicalAttributes.passing),
                goeStat(c.getPenaltyTaking(), fmPlayer.technicalAttributes.penaltyTaking),
                goeStat(c.getTackling(), fmPlayer.technicalAttributes.tackling),
                goeStat(c.getTechnique(), fmPlayer.technicalAttributes.technique),

                // Mental
                goeStat(c.getAggression(), fmPlayer.mentalAttributes.aggression),
                goeStat(c.getAnticipation(), fmPlayer.mentalAttributes.anticipation),
                goeStat(c.getBravery(), fmPlayer.mentalAttributes.bravery),
                goeStat(c.getComposure(), fmPlayer.mentalAttributes.composure),
                goeStat(c.getConcentration(), fmPlayer.mentalAttributes.concentration),
                goeStat(c.getDecisions(), fmPlayer.mentalAttributes.decisions),
                goeStat(c.getDetermination(), fmPlayer.mentalAttributes.determination),
                goeStat(c.getFlair(), fmPlayer.mentalAttributes.flair),
                goeStat(c.getLeadership(), fmPlayer.mentalAttributes.leadership),
                goeStat(c.getOffTheBall(), fmPlayer.mentalAttributes.offTheBall),
                goeStat(c.getPositioning(), fmPlayer.mentalAttributes.positioning),
                goeStat(c.getTeamwork(), fmPlayer.mentalAttributes.teamwork),
                goeStat(c.getVision(), fmPlayer.mentalAttributes.vision),
                goeStat(c.getWorkRate(), fmPlayer.mentalAttributes.workRate),

                // Physical
                goeStat(c.getAcceleration(), fmPlayer.physicalAttributes.acceleration),
                goeStat(c.getAgility(), fmPlayer.physicalAttributes.agility),
                goeStat(c.getBalance(), fmPlayer.physicalAttributes.balance),
                goeStat(c.getJumpingReach(), fmPlayer.physicalAttributes.jumpingReach),
                goeStat(c.getNaturalFitness(), fmPlayer.physicalAttributes.naturalFitness),
                goeStat(c.getPace(), fmPlayer.physicalAttributes.pace),
                goeStat(c.getStamina(), fmPlayer.physicalAttributes.stamina),
                goeStat(c.getStrength(), fmPlayer.physicalAttributes.strength),

                // Position
                goeStat(c.getGK(), fmPlayer.position.goalkeeper),
                goeStat(c.getLB(), fmPlayer.position.defenderLeft),
                goeStat(c.getCB(), fmPlayer.position.defenderCentral),
                goeStat(c.getRB(), fmPlayer.position.defenderRight),
                goeStat(c.getLWB(), fmPlayer.position.wingBackLeft),
                goeStat(c.getRWB(), fmPlayer.position.wingBackRight),
                goeStat(c.getDM(), fmPlayer.position.defensiveMidfielder),
                goeStat(c.getLM(), fmPlayer.position.midfielderLeft),
                goeStat(c.getCM(), fmPlayer.position.midfielderCentral),
                goeStat(c.getRM(), fmPlayer.position.midfielderRight),
                goeStat(c.getLAM(), fmPlayer.position.attackingMidLeft),
                goeStat(c.getCAM(), fmPlayer.position.attackingMidCentral),
                goeStat(c.getRAM(), fmPlayer.position.attackingMidRight),
                goeStat(c.getST(), fmPlayer.position.striker)
        );
    }

    // 18 50 -> 2007, 1975
    private BooleanExpression ageBetween(Integer ageMin, Integer ageMax) {
        if (ageMin == null || ageMax == null) {
            return null;
        }
        LocalDate today = timeProvider.getCurrentLocalDate();
        LocalDate toBirth = today.minusYears(ageMin);
        LocalDate fromBirth = today.minusYears(ageMax);
        return player.birth.between(fromBirth, toBirth);
    }

    private BooleanExpression goeStat(Integer stat, NumberExpression<Integer> expr) {
        if (stat == null) {
            return null;
        }
        return expr.goe(stat);
    }

    private BooleanExpression teamIdEq(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return player.team.id.eq(teamId);
    }

    private BooleanExpression leagueIdEq(Long leagueId) {
        if (leagueId == null) {
            return null;
        }
        return player.team.league.id.eq(leagueId);
    }
}



