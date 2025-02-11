package com.ksy.fmrs.repository.Team;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.domain.QPlayer;
import com.ksy.fmrs.domain.QTeam;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;

}
