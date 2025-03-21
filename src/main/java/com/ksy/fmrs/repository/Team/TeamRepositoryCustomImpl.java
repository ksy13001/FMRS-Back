package com.ksy.fmrs.repository.Team;

import com.ksy.fmrs.domain.QTeam;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.ksy.fmrs.domain.QTeam.team;

@RequiredArgsConstructor
@Repository
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    // 1000개 씩 업데이트(in query는 1000개가 최대)
    @Override
    public void resetAllTeamsSquad(List<Long> teamId) {
        queryFactory.update(team)
                .set(team.players, new ArrayList<>())
                .where(team.id.in(teamId))
                .execute();
    }
}
