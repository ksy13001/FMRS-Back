package com.ksy.fmrs.config;

import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.enums.Role;
import com.ksy.fmrs.exception.UnauthorizedException;
import com.ksy.fmrs.security.JwtTokenProvider;
import com.ksy.fmrs.util.Jti.JtiProvider;
import com.ksy.fmrs.util.time.TimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private JtiProvider jtiProvider;
    @Mock
    private SecretKey secretKey;
    private User testUser;

    private final String jwtSecret = "12345678901234567890123456789012345678901234";
    private final long accessTokenValidityMs = 3600000L;   // 1h
    private final long refreshTokenValidityMs = 604800000L; // 7 days


    @BeforeEach
    void setUp() {
        this.testUser = User.builder()
                .username("user1")
                .password("password1")
                .role(Role.ROLE_USER)
                .build();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        this.secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        this.jwtTokenProvider = new JwtTokenProvider(
                secretKey, timeProvider, accessTokenValidityMs, refreshTokenValidityMs, jtiProvider
        );
    }

    @Test
    @DisplayName("access 토큰의 유효 기간 1시간")
    void access_token_expire_1hour() {
        // given
        Date now = new Date();
        // when
        when(timeProvider.getCurrentDate()).thenReturn(now);
        String token = jwtTokenProvider.generateJwtAccessToken(testUser.getId(), testUser.getUsername());
        Claims actual = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // then
        Assertions.assertThat(actual.getSubject())
                .isEqualTo(String.valueOf(testUser.getId()));
        Assertions.assertThat(Math.abs(actual.getExpiration().getTime() - (now.getTime() + accessTokenValidityMs)))
                .isLessThan(1000L);
    }

    @Test
    @DisplayName("refresh 토큰의 유효 기간 7일")
    void refresh_token_expire_7days() {
        // given
        Date now = new Date();
        // when
        when(timeProvider.getCurrentDate()).thenReturn(now);
        String token = jwtTokenProvider.generateJwtRefreshToken(testUser.getId(), testUser.getUsername());
        Claims actual = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // then
        Assertions.assertThat(actual.getSubject())
                .isEqualTo(String.valueOf(testUser.getId()));
        Assertions.assertThat(Math.abs(actual.getExpiration().getTime() - (now.getTime() + refreshTokenValidityMs)))
                .isLessThan(1000L);
    }

    @Test
    @DisplayName("유효기간 지난 토큰 파싱 시 예외처리")
    void expired_access_token(){
        // given
        String expiredAccess = Jwts.builder()
                .expiration(Date.from(Instant.now().minus(1000L, ChronoUnit.MILLIS)))
                .signWith(secretKey)
                .compact();

        // when && then
        Assertions.assertThatThrownBy(
                ()->jwtTokenProvider.parseAndValidateToken(expiredAccess))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("만료된 JWT 토큰입니다");

    }
}