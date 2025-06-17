package com.ksy.fmrs.repository.Team;

import com.ksy.fmrs.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
    Optional<Team> findTeamByTeamApiId(Integer teamApiId);
    @Query("select t from Team t " +
            "join fetch t.league")
    List<Team> findAllTeamsWithLeague();

    List<Team> findAllByNameStartingWithOrderByNameAsc(String name);
}
