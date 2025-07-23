package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.user.*;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.security.TokenResolver;
import com.ksy.fmrs.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ksy.fmrs.domain.enums.TokenType.ACCESS_TOKEN;
import static com.ksy.fmrs.domain.enums.TokenType.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenResolver tokenResolver;

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        TokenPairWithId tokenPairWithId;
        try {
            tokenPairWithId = authService.login(dto.username(), dto.password());
        } catch (AuthenticationException e) {
            return LoginResponseDto.authenticationFailed();
        }
        addTokensToCookie(response, tokenPairWithId.access(), tokenPairWithId.refresh());
        return LoginResponseDto.success(tokenPairWithId.userId(), dto.username());
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        removeTokensFromCookie(response);
        return tokenResolver.extractTokenFromCookie(request, REFRESH_TOKEN.getType())
                .map(authService::logout)
                .orElseGet(ResponseEntity.badRequest()::build);
    }

    @PostMapping("/api/auth/reissue")
    public ResponseEntity<ReissueResponseDto> reissue(HttpServletRequest request, HttpServletResponse response) {
        return tokenResolver.extractTokenFromCookie(request, REFRESH_TOKEN.getType())
                .map(oldRefresh -> {
                    TokenPair tokenPair = authService.reissueToken(oldRefresh);
                    addTokensToCookie(response, tokenPair.access(), tokenPair.refresh());
                    return ReissueResponseDto.success();
                })
                .orElse(ReissueResponseDto.badRequest());
    }

    @GetMapping("/api/auth/status")
    public ResponseEntity<LoginStatusResponseDto> status(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return LoginStatusResponseDto.authenticated(userDetails.getId(), userDetails.getUsername());
    }

    private void addTokensToCookie(HttpServletResponse response, String access, String refresh) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(ACCESS_TOKEN.getType(), access, ACCESS_TOKEN.getExp()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(REFRESH_TOKEN.getType(), refresh, REFRESH_TOKEN.getExp()).toString());
    }

    private void removeTokensFromCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(ACCESS_TOKEN.getType(), "", 0).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                createCookie(REFRESH_TOKEN.getType(), "", 0).toString());
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
