package com.ksy.fmrs.security;

import com.ksy.fmrs.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserDetailsService userDetailsService;

    private final String bearerToken = "Bearer ";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        // verify 시 mock 이나 spy 된 객체에 대해서만 호출 가능
        this.filterChain = spy(new MockFilterChain());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("헤더에 Authorization 존재하지 않을 경우, 인증정보 세팅하지 않고 다음 필터로 전달")
    void Header_without_Authorization() throws ServletException, IOException {
        // given

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
        verify(filterChain, times(1))
                .doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 Bearer 로 시작하지 않을 경우, 인증정보 세팅하지 않고 다음 필터로 전달")
    void Authorization_not_start_with_Bearer () throws ServletException, IOException {
        // given
        request.addHeader("Authorization", "Bearer123123123");
        // when

        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
        verify(filterChain, times(1))
                .doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 만료된 경우, 인증정보 세팅하지 않고 다음 필터로 전달하지 않고 애러 메시지 반환")
    void DoFilterInternal_with_expired_token() throws ServletException, IOException {
        // given
        String token = "token";
        request.addHeader("Authorization", bearerToken + token);

        // when
        doThrow(ExpiredJwtException.class).when(jwtTokenProvider).validateToken(token);
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 유효하지 않은 경우, 인증정보 세팅하지 않고 다음 필터로 전달하지 않고 애러 메시지 반환")
    void DoFilterInternal_with_invalid_token() throws ServletException, IOException {
        // given
        String token = "token";
        request.addHeader("Authorization", bearerToken + token);

        // when
        doThrow(JwtException.class).when(jwtTokenProvider).validateToken(token);
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();
        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    @DisplayName("인증 정보 세팅")
    void doFilterInternal() throws ServletException, IOException {
        // given
        String username = "username";
        String password = "password";
        User user = User.builder()
                .username(username)
                .password(password)
                .build();

        UserDetails userDetails = new CustomUserDetails(user);
        String token = "123123123";
        request.addHeader("Authorization", bearerToken + token);

        // when
        doNothing().when(jwtTokenProvider).validateToken(token);
        when(jwtTokenProvider.getEmailFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        Assertions.assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNotNull();
        verify(filterChain, times(1))
                .doFilter(request, response);
    }
}