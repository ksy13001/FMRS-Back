package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {

    List<Player> findAllByTeamId(Long teamId);

    Optional<Player> findByPlayerApiId(Integer playerApiId);

    List<Player> findByMappingStatus(PlayerMappingStatus mappingStatus);

    @Query("SELECT p FROM Player p " +
            "JOIN FmPlayer fp " +
            "ON p.firstName = fp.firstName " +
            "AND p.lastName = fp.lastName " +
            "AND p.birth = fp.birth " +
            "AND p.nationName = fp.nationName " +
            "WHERE p.mappingStatus = 'UNMAPPED' " +
            "GROUP BY p.id, p.firstName, p.lastName, p.birth, p.nationName " +
            "HAVING COUNT(fp.id) > 1")
    List<Player> findPlayerDuplicatedWithFmPlayer();

//    @Query("SELECT p FROM Player p " +
//            "GROUP BY p.firstName, p.lastName, p.birth, p.nationName " +
//            "HAVING COUNT(p) > 1 ")
//    List<Player> findDuplicatedPlayers();
}