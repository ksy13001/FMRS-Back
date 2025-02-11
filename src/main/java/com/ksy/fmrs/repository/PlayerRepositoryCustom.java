package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Player;
import com.ksy.fmrs.dto.SearchPlayerCondition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepositoryCustom {
    List<Player> searchPlayerByName(String name);
    List<Player> searchPlayerByDetailCondition(SearchPlayerCondition condition);
}
