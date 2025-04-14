package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.player.PlayerRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRawRepository extends JpaRepository<PlayerRaw, Integer> {

}
