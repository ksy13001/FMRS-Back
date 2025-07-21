package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.user.*;
import com.ksy.fmrs.service.user.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final int REFRESH_EXP = 7 * 24 * 60 * 60;
    private static final int ACCESS_EXP = 30 * 60;

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        TokenPairWithId tokenPairWithId;
        try {
            tokenPairWithId = authService.login(dto.username(), dto.password());
        } catch (AuthenticationException e) {
            return LoginResponseDto.authenticationFailed();
        }
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(ACCESS_TOKEN, tokenPairWithId.access(), ACCESS_EXP).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(REFRESH_TOKEN, tokenPairWithId.refresh(), REFRESH_EXP).toString());

        return LoginResponseDto.success(tokenPairWithId.userId(), dto.username());
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,  HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(ACCESS_TOKEN, "", 0).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(REFRESH_TOKEN, "", 0).toString());
        return authService.logout(extractTokenFromCookie(request));
    }

    @PostMapping("/api/auth/reissue")
    public ResponseEntity<ReissueResponseDto> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info(request.getCookies().toString());
        TokenPair tokenPair = authService.reissueToken(extractTokenFromCookie(request));
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(ACCESS_TOKEN, tokenPair.access(), ACCESS_EXP).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(REFRESH_TOKEN, tokenPair.refresh(), REFRESH_EXP).toString());

        return ReissueResponseDto.success();
    }

    @GetMapping("/api/auth/status")
    public ResponseEntity<Void> validate() {
        return ResponseEntity.ok().build();
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            throw new IllegalStateException("Cookie is null");
        }

        return Arrays.stream(cookies)
                .filter(c -> REFRESH_TOKEN.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("쿠키에서 리프레시 토큰을 찾을수없습니다"));
    }

    private ResponseCookie createCookie(String tokenName, String tokenValue, int maxAge) {
        return ResponseCookie.from(tokenName, tokenValue)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("None")
                .build();
    }
}
