package com.ksy.fmrs.service.user;

import com.ksy.fmrs.domain.BlackList;
import com.ksy.fmrs.domain.RefreshToken;
import com.ksy.fmrs.domain.enums.TokenType;
import com.ksy.fmrs.dto.user.TokenPair;
import com.ksy.fmrs.dto.user.TokenPairWithId;
import com.ksy.fmrs.repository.user.BlackListRepository;
import com.ksy.fmrs.repository.user.RefreshTokenRepository;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.security.JwtTokenProvider;
import com.ksy.fmrs.security.TokenValidator;
import com.ksy.fmrs.util.time.TimeProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    @Value("${spring.jwt.refreshTokenValidityMs}")
    private Long refreshTokenValidityMs;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenValidator tokenValidator;
    private final BlackListRepository blackListRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final TimeProvider timeProvider;

    @Transactional
    public TokenPairWithId login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long userId = userDetails.getId();

        String access = generateJwtAccessToken(userId, username);
        String refresh = generateJwtRefreshToken(userId, username);

        saveRefreshToken(userId, refresh);

        return new TokenPairWithId(userId, access, refresh);
    }

    @Transactional
    public ResponseEntity<Void> logout(Long userId, String oldRefresh) {
        Claims claims = jwtTokenProvider.parseAndValidateToken(
                oldRefresh);

        validateToken(claims, userId, TokenType.REFRESH_TOKEN);

        String jti = claims.getId();
        Date expiryDate = claims.getExpiration();

        saveBlackListToken(userId, jti, expiryDate.toInstant());
        refreshTokenRepository.deleteByToken(oldRefresh);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public TokenPair reissueToken(Long userId, String oldRefresh) {
        // 검증시 통일된 예외처리 필요
        // 토큰 검증
        Claims claims = jwtTokenProvider.parseAndValidateToken(
                oldRefresh);

        String username = claims.get("username", String.class);
        Date expiryDate = claims.getExpiration();
        String oldJti = claims.getId();

        validateToken(claims, userId, TokenType.REFRESH_TOKEN);

        String access  = generateJwtAccessToken(userId, username);
        String refresh = generateJwtRefreshToken(userId, username);
        rotateRefreshToken(userId, oldJti, expiryDate.toInstant(), refresh);

        return new TokenPair(access, refresh);
    }


    private void rotateRefreshToken(Long userId, String oldJti, Instant expiry, String newRefresh) {
        saveBlackListToken(userId, oldJti, expiry);
        saveRefreshToken(userId, newRefresh);
    }

    private String generateJwtAccessToken(Long userId, String username) {
        return jwtTokenProvider.generateJwtAccessToken(userId, username);
    }

    private String generateJwtRefreshToken(Long userId, String username) {
        return jwtTokenProvider.generateJwtRefreshToken(userId, username);
    }

    private void saveRefreshToken(Long userId, String token) {
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiryDate(timeProvider.getCurrentInstant().plus(refreshTokenValidityMs, ChronoUnit.MILLIS))
                .build());
    }

    private void saveBlackListToken(Long userId, String jti, Instant expiry) {
        blackListRepository.save(BlackList.builder()
                .userId(userId)
                .refreshJti(jti)
                .expiryDate(expiry)
                .build());
    }

    private void validateToken(Claims claims, Long userId, TokenType tokenType) {
        tokenValidator.validateTokenInBlacklist(claims);
        tokenValidator.validateUserId(claims, userId);
        tokenValidator.validateTokenType(claims, tokenType);
    }
}
