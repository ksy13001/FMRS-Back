package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {

    List<Player> findAllByTeamId(Long teamId);

    Optional<Player> findByPlayerApiId(Integer playerApiId);
}