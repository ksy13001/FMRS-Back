package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepositoryCustom {
    List<Player> searchPlayerByName(String name);

    List<Player> searchPlayerByDetailCondition(SearchPlayerCondition condition);

    List<Player> searchPlayerByLastNameAndAge(String lastName, Integer age);
}
