package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {

    List<Player> findAllByTeamId(Long teamId);

    Optional<Player> findByPlayerApiId(Integer playerApiId);

    @Query("SELECT p FROM Player p " +
            "JOIN FETCH p.team t " +
            "JOIN FETCH t.league " +
            "WHERE p.id = :id")
    Optional<Player> findWithTeamLeagueById(Long id);

    @Query("SELECT p FROM Player p " +
            "WHERE p.playerApiId IN :playerApiIds")
    List<Player> findByPlayerApiIdIn(Collection<Integer> playerApiIds);

    List<Player> findByMappingStatus(MappingStatus mappingStatus);

//    @Query("SELECT p FROM Player p " +
//            "join fetch  ps" +
//            "WHERE p.mappingStatus = 'UNMAPPED' " +
//            "")
//    List<Player> findUnMappingPlayerWithPlayerStat();

    @Query("SELECT p FROM Player p " +
            "JOIN fmplayer fp " +
            "ON p.firstName = fp.firstName " +
            "AND p.lastName = fp.lastName " +
            "AND p.birth = fp.birth " +
            "AND p.nationName = fp.nationName " +
            "WHERE p.mappingStatus = 'UNMAPPED' " +
            "GROUP BY p.id, p.firstName, p.lastName, p.birth, p.nationName " +
            "HAVING COUNT(fp.id) > 1")
    List<Player> findPlayerDuplicatedWithFmPlayer();

    @Query("SELECT DISTINCT p.nationName FROM Player p")
    List<String> getNationNamesFromPlayers();
//    @Query("SELECT p FROM Player p " +
//            "GROUP BY p.firstName, p.lastName, p.birth, p.nationName " +
//            "HAVING COUNT(p) > 1 ")
//    List<Player> findDuplicatedPlayers();

    @Query("SELECT p.playerApiId FROM Player p")
    Set<Integer> findAllPlayerApiId();
}