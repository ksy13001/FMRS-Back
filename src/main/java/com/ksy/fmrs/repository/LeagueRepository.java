package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.League;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    Optional<League> findLeagueByLeagueApiId(Integer leagueApiId);
}
