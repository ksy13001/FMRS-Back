package com.ksy.fmrs.repository.Team;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.LeagueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
    Optional<Team> findByTeamApiId(Integer teamApiId);

    List<Team> findByTeamApiIdIn(Collection<Integer> teamApiIds);

    @Query("SELECT t FROM Team t " +
            "JOIN t.league")
    List<Team> findAllTeamsWithLeague();

    List<Team> findAllByNameStartingWithOrderByNameAsc(String name);

    @Query("SELECT t FROM Team t " +
            "JOIN t.league " +
            "WHERE t.league.leagueType= :type ")
    List<Team> findTeamsByLeagueType(@Param("type") LeagueType type);

    @Query("select t.teamApiId from Team t")
    List<Integer> findTeamApiIds();
}
