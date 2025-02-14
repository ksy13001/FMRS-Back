package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long>, PlayerRepositoryCustom {

    public List<Player> findAllByTeamId(Long teamId);
    public List<Player> findAllByOrderByMarketValueDesc();
}
