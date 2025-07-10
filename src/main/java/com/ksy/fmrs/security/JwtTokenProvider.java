package com.ksy.fmrs.security;

import com.ksy.fmrs.domain.enums.TokenType;
import com.ksy.fmrs.exception.UnauthorizedException;
import com.ksy.fmrs.util.Jti.JtiProvider;
import com.ksy.fmrs.util.time.TimeProvider;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider{

    private final JtiProvider jtiProvider;
    private final TimeProvider timeProvider;
    private final SecretKey secretKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;


    public JwtTokenProvider(SecretKey secretKey,  TimeProvider timeProvider,
                            @Value("${spring.jwt.accessTokenValidityMs}") Long accessTokenValidityMs,
                            @Value("${spring.jwt.refreshTokenValidityMs}") Long refreshTokenValidityMs,
                            JtiProvider jtiProvider) {
        this.timeProvider = timeProvider;
        this.secretKey = secretKey;
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
        this.jtiProvider = jtiProvider;
    }

    public String generateJwtAccessToken(Long userId, String username) {
        Date now = timeProvider.getCurrentDate();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityMs);

        return Jwts.builder()
                .id(jtiProvider.generateJti())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", TokenType.ACCESS_TOKEN.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String generateJwtRefreshToken(Long userId, String username) {
        Date now = timeProvider.getCurrentDate();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .id(jtiProvider.generateJti())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", TokenType.REFRESH_TOKEN.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseAndValidateToken(token)
                .get("username", String.class);
    }


    public Claims parseAndValidateToken(String token) {
        // 토큰 유효성 검증
        try{
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            throw new UnauthorizedException("JWT 토큰값이 잘못되었습니다.", e);
        } catch (ExpiredJwtException e){
            throw new UnauthorizedException("만료된 JWT 토큰입니다", e);
        } catch (UnsupportedJwtException e) {
            throw new UnauthorizedException("지원되지 않는 JWT 토큰입니다", e);
        } catch (IllegalStateException e){
            throw new UnauthorizedException("JWT 토큰이 존재하지 않습니다", e);
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
