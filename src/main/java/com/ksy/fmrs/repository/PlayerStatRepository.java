package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.PlayerStat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface PlayerStatRepository extends CrudRepository<PlayerStat, Long> {

}
