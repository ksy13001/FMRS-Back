package com.ksy.fmrs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.TokenType;
import com.ksy.fmrs.dto.user.LoginRequestDto;
import com.ksy.fmrs.dto.user.TokenPair;
import com.ksy.fmrs.dto.user.TokenPairWithId;
import com.ksy.fmrs.security.JwtFilter;
import com.ksy.fmrs.security.JwtTokenProvider;
import com.ksy.fmrs.security.TokenResolver;
import com.ksy.fmrs.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser("test_user")
@AutoConfigureMockMvc(addFilters = false) // 필터 제거
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private TokenResolver tokenResolver;

    @Test
    @DisplayName("로그인 성공시 HTTPONLY 쿠키에 토큰 설정, " +
            "userId, userName 바디에 반환")
    void login_success() throws Exception {
        // given
        Long userId = 1L;
        String username = "user";
        String password = "password";
        String accessToken = "test_access_token";
        String refreshToken = "test_refresh_token";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);
        TokenPairWithId tokenPairWithId = new TokenPairWithId(userId, accessToken, refreshToken);
        given(authService.login(username, password)).willReturn(
                tokenPairWithId
        );

        // when
        MockHttpServletResponse response = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto))
                        .with(csrf()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.username").value(username))
                .andReturn()
                .getResponse();

        // then
        Cookie[] cookies = response.getCookies();

        tokenTest(cookies, TokenType.REFRESH_TOKEN.getType(), refreshToken, TokenType.REFRESH_TOKEN.getExp());
        tokenTest(cookies, TokenType.ACCESS_TOKEN.getType(), accessToken, TokenType.ACCESS_TOKEN.getExp());
    }

    @Test
    @DisplayName("로그인 실패시, 401 반환")
    void login_failed() throws Exception {
        // given
        String username = "invalid_user";
        String password = "invalid_password";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username, password);
        given(authService.login(username, password)).willThrow(BadCredentialsException.class);
        // when
        ResultActions actions = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .with(csrf())
        );

        // then
        actions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("로그아웃 시, 쿠키의 토큰 모두 제거(만료기간 즉시로 추가)")
    void logout_success() throws Exception {
        // given
        String oldRefresh = "old_refresh";
        given(tokenResolver.extractTokenFromCookie(any(HttpServletRequest.class), anyString()))
                .willReturn(Optional.of(oldRefresh));
        given(authService.logout(oldRefresh))
                .willReturn(ResponseEntity.ok().build());

        // when
        ResultActions actions = mvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));

        // then
        actions.andExpect(status().isOk());
        Cookie[] cookies = actions.andReturn()
                .getResponse().getCookies();
        tokenTest(cookies, TokenType.ACCESS_TOKEN.getType(), "", 0);
        tokenTest(cookies, TokenType.REFRESH_TOKEN.getType(), "", 0);
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 토큰 재발급 요청시, 기존 리프레시 토큰 삭제 및 새 토큰 쌍 쿠키에 저장")
    void reissue_success() throws Exception {
        // given
        String oldRefresh = "old_refresh";
        String newRefresh = "new_refresh";
        String newAccess = "new_access";
        TokenPair tokenPair = new TokenPair(newAccess, newRefresh);

        given(tokenResolver.extractTokenFromCookie(any(HttpServletRequest.class), anyString()))
                .willReturn(Optional.of(oldRefresh));
        given(authService.reissueToken(oldRefresh))
                .willReturn(tokenPair);
        // when

        ResultActions actions = mvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()));
        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        Cookie[] cookies = actions.andReturn().getResponse().getCookies();
        tokenTest(cookies, TokenType.ACCESS_TOKEN.getType(), newAccess, TokenType.ACCESS_TOKEN.getExp());
        tokenTest(cookies, TokenType.REFRESH_TOKEN.getType(), newRefresh, TokenType.REFRESH_TOKEN.getExp());
    }

    void tokenTest(Cookie[] cookies, String tokenType, String tokenValue, int maxAge) {
        Assertions.assertThat(cookies)
                .filteredOn(cookie -> Objects.equals(cookie.getName(), tokenType))
                .singleElement()
                .satisfies(token -> {
                    assertThat(token.getValue()).isEqualTo(tokenValue);
                    assertThat(token.getSecure()).isTrue();
                    assertThat(token.isHttpOnly()).isTrue();
                    assertThat(token.getMaxAge()).isEqualTo(maxAge);
                });
    }
}