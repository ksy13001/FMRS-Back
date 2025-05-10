package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.search.SearchPlayerCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlayerRepositoryCustom {
    Slice<Player> searchPlayerByName(
            String name, Pageable pageable, MappingStatus mappingStatus, Integer lastCurrentAbility, Long lastPlayerId);

    List<Player> searchPlayerByDetailCondition(SearchPlayerCondition condition);

    List<Player> searchPlayerByFm(String firstName, String lastName, LocalDate birth, String nation);

    Long updateDuplicatedUnmappedPlayersToFailed();

    List<Player> findDuplicatedPlayers();
}
