package com.ksy.fmrs.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.ksy.fmrs.domain.enums.TokenType.ACCESS_TOKEN;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenResolver tokenResolver;

    public JwtFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService,  TokenResolver tokenResolver) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenResolver = tokenResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {

        Optional<String> token = tokenResolver.extractTokenFromCookie(request, ACCESS_TOKEN.getType());
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = token.get();

        try {
            jwtTokenProvider.parseAndValidateToken(accessToken);
            setAuthenticateContext(accessToken);
        } catch (ExpiredJwtException e) {
            log.debug("Expired access token. Continue request as unauthenticated.");
            SecurityContextHolder.clearContext();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid access token. Continue request as unauthenticated.");
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    
    private void setAuthenticateContext(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
