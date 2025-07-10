package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.user.*;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.service.user.AuthService;
import com.ksy.fmrs.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private static final String REFRESH = "refreshToken";

    private final AuthService authService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {
        TokenPairWithId tokenPairWithId = authService.login(dto.username(), dto.password());
        if (tokenPairWithId == null) {
            return LoginResponseDto.authenticationFailed();
        }

        Cookie refreshTokenCookie = createCookie(REFRESH, tokenPairWithId.refresh());
        response.setHeader("Authorization", "Bearer " + tokenPairWithId.access());
        response.addCookie(refreshTokenCookie);

        return LoginResponseDto.success(tokenPairWithId.userId(), dto.username());
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       HttpServletRequest request) {
        return authService.logout(userDetails.getId(), extractToken(request));
    }

    @PostMapping("/api/auth/reissue")
    public ResponseEntity<ReissueResponseDto> reissue(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      HttpServletRequest request, HttpServletResponse response) {
        TokenPair tokenPair = authService.reissueToken(userDetails.getId(), extractToken(request));
        response.setHeader("Authorization", "Bearer " + tokenPair.access());
        response.addCookie(createCookie(REFRESH, tokenPair.refresh()));

        return ReissueResponseDto.success();
    }

    @GetMapping("/api/auth/status")
    public ResponseEntity<Void> validate() {
        return ResponseEntity.ok().build();
    }

    private String extractToken(HttpServletRequest request) {
        return StringUtils.extractTokenFromBearer(request.getHeader("Authorization"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Authorization Header"));
    }


    private Cookie createCookie(String tokenName, String tokenValue) {
        Cookie accessTokenCookie = new Cookie(tokenName, tokenValue);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS에서만
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(30 * 60); // 30분
        return accessTokenCookie;
    }
}
