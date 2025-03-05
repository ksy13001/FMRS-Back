package com.ksy.fmrs.repository.Team;

import com.ksy.fmrs.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, TeamRepositoryCustom {
    Optional<Team> findTeamByTeamApiId(Integer teamApiId);
}
