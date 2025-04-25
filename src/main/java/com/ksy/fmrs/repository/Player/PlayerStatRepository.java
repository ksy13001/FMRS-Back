package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.player.PlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {
}
