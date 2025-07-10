package com.ksy.fmrs.repository.user;

import com.ksy.fmrs.domain.RefreshToken;
import com.ksy.fmrs.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByToken(String token);
}
