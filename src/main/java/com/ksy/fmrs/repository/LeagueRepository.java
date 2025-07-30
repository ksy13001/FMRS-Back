package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findLeagueByLeagueApiId(Integer leagueApiId);

    @Query("SELECT l.leagueApiId FROM League l")
    Set<Integer> findAllLeagueApiIds();

    @Query("SELECT l FROM League l " +
            "WHERE l.teams IS EMPTY")
    List<League> findUnassignedLeagues();
}
