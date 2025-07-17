package com.ksy.fmrs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.dto.user.LoginRequestDto;
import com.ksy.fmrs.dto.user.TokenPairWithId;
import com.ksy.fmrs.security.JwtFilter;
import com.ksy.fmrs.security.JwtTokenProvider;
import com.ksy.fmrs.service.user.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final String REFRESH = "refresh_token";

    @Test
    @DisplayName("로그인 성공시 Authorization 헤더와 HTTPONLY 쿠키에 토큰 설정, " +
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
        ResultActions actions = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
                .with(csrf())
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION
                        , "Bearer " + accessToken))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        Matchers.startsWith(REFRESH + "=" + refreshToken + ";")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        Matchers.containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        Matchers.containsString("Secure")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        Matchers.containsString("SameSite=None")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.username").value(username));
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
//
//    @Test
//    @DisplayName("로그아웃시, 쿠키에서 리프레시 토큰 추출 후 서비스 레이어에 전달 및 쿠키 삭제")
//    void logout_success() throws Exception {
//        // given
//        HttpServletRequest request = new MockHttpServletRequest();
//        String oldRefresh = "refreshToken";
//        MockCookie cookie = new MockCookie(REFRESH, oldRefresh);
//        request.setAttribute(REFRESH, cookie);
//
//        given(authService.logout(oldRefresh)).willReturn(ResponseEntity.ok().build());
//        // when
//        ResultActions actions = mvc.perform(post("api/auth/logout")
//                .contentType(MediaType.APPLICATION_JSON)
//                        .cookie(cookie)
//                        .with(csrf()));
//
//        // then
//        actions.andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.SET_COOKIE,
//                        Matchers.startsWith(REFRESH + "=" + "" + ";")));
//    }

}