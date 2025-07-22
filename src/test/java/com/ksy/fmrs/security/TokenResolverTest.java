package com.ksy.fmrs.security;

import com.ksy.fmrs.domain.enums.TokenType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;


class TokenResolverTest {

    private TokenResolver tokenResolver;

    @BeforeEach
    void setUp() {
        tokenResolver = new TokenResolver();
    }

    @Test
    @DisplayName("쿠키에 토큰 있으면, 토큰 추출-반환")
    void extractTokenFromCookie_success() {
        // given
        String refresh = "refresh";
        String access =  "access";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new MockCookie(TokenType.ACCESS_TOKEN.getType(), access),
                new MockCookie(TokenType.REFRESH_TOKEN.getType(), refresh)
        );

        // when
        Optional<String> actual = tokenResolver
                .extractTokenFromCookie(request, TokenType.ACCESS_TOKEN.getType());

        // then
        Assertions.assertThat(actual.get())
                .isEqualTo(access);
    }

    @Test
    @DisplayName("쿠키에 토큰 없으면, Optional.empty 반환")
    void extractTokenFromCookie_badRequest() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        Optional<String> actual = tokenResolver
                .extractTokenFromCookie(request, TokenType.ACCESS_TOKEN.getType());

        // then
        Assertions.assertThat(actual)
                .isEqualTo(Optional.empty());
    }
}