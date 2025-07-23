package com.ksy.fmrs.repository.user;

import com.ksy.fmrs.domain.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    Boolean existsBlackListsByRefreshJti(String jti);

    List<BlackList> findBlackListByExpiryDateBefore(Instant now);

    @Modifying(clearAutomatically = true)
    @Query("DELETE BlackList b WHERE b.expiryDate < :now")
    void deleteAllByExpiryDateBefore(Instant now);
}
