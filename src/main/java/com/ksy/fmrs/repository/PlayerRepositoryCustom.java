package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.dto.PlayerSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepositoryCustom {

    List<Player> searchPlayer(PlayerSearchCondition condition);
}
