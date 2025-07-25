package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findLeagueByLeagueApiId(Integer leagueApiId);
}
