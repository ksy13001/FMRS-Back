package com.ksy.fmrs.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

import static com.ksy.fmrs.domain.enums.TokenType.REFRESH_TOKEN;

@Slf4j
@Component
public class TokenResolver {

    public Optional<String> extractTokenFromCookie(HttpServletRequest request, String tokenType) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(tokenType))
                .findFirst()
                .map(Cookie::getValue));
    }
}
