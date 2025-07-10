package com.ksy.fmrs.repository.user;

import com.ksy.fmrs.domain.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    Boolean existsBlackListsByRefreshJti(String jti);
}
