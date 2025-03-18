package com.ksy.fmrs.repository.Team;

import com.ksy.fmrs.domain.QTeam;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import static com.ksy.fmrs.domain.QTeam.team;

@RequiredArgsConstructor
@Repository
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public void resetAllTeamsSquad() {
        queryFactory.update(team)
                .set(team.players, new ArrayList<>())
                .execute();
    }
}
