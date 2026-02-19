package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.BlackList;
import com.ksy.fmrs.domain.RefreshToken;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.enums.Role;
import com.ksy.fmrs.domain.enums.TokenType;
import com.ksy.fmrs.dto.user.TokenPair;
import com.ksy.fmrs.dto.user.TokenPairWithId;
import com.ksy.fmrs.exception.UnauthorizedException;
import com.ksy.fmrs.repository.BlackListRepository;
import com.ksy.fmrs.repository.RefreshTokenRepository;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.security.JwtTokenProvider;
import com.ksy.fmrs.security.TokenValidator;
import com.ksy.fmrs.util.time.TimeProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private TokenValidator tokenValidator;
    @Mock
    private BlackListRepository blackListRepository;

    private User testUser;
    private String accessToken;
    private String refreshToken;
    private Instant now;
    private Instant expiry;
    private CustomUserDetails userDetails;
    private Authentication authentication;
    private Claims claims;
    private String jti;

    private static final Long refreshTokenValidityMs = 604800000L;


    @BeforeEach
    void setUp() {
        Long userId = 1L;
        String username = "testUser1";
        String password = "@@password1234@@";
        this.accessToken = "mockAccessToken";
        this.refreshToken = "mockRefreshToken";
        this.now = Instant.now();
        this.expiry = this.now.plusMillis(refreshTokenValidityMs);
        this.jti = "jti";

        this.testUser = createUser(userId, username, password);
        this.userDetails = new CustomUserDetails(testUser);
        this.authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        this.claims = Jwts.claims()
                .id(jti)
                .subject(userId.toString())
                .add("username", username)
                .expiration(Date.from(this.expiry))
                .build();
        ReflectionTestUtils.setField(authService, "refreshTokenValidityMs", 604800000L);
    }

    @Test
    @DisplayName("username, password 입력으로 로그인 성공 시, userId/access/refresh 토큰 반환")
    void login_success() {
        // given
        TokenPairWithId expect = new TokenPairWithId(
                testUser.getId(), accessToken, refreshToken);
        // when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateJwtAccessToken(testUser.getId(), testUser.getUsername()))
                .thenReturn(accessToken);
        when(jwtTokenProvider.generateJwtRefreshToken(testUser.getId(), testUser.getUsername()))
                .thenReturn(refreshToken);
        when(timeProvider.getCurrentInstant()).thenReturn(now);
        TokenPairWithId actual = authService.login(testUser.getUsername(), testUser.getPassword());

        // then
        Assertions.assertThat(actual.access()).isEqualTo(expect.access());
        Assertions.assertThat(actual.refresh()).isEqualTo(expect.refresh());
        Assertions.assertThat(actual.userId()).isEqualTo(expect.userId());

        ArgumentCaptor<RefreshToken> refreshCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(1)).save(refreshCaptor.capture());
        Assertions.assertThat(refreshCaptor.getValue().getToken()).isEqualTo(refreshToken);
        Assertions.assertThat(refreshCaptor.getValue().getExpiryDate()).isEqualTo(expiry);
        Assertions.assertThat(refreshCaptor.getValue().getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("비밀번호 틀릴경우 BadCredentialsException")
    void login_with_wrong_password() {
        // given
        String wrongPassword = "@@wrongpassword@@";
        String msg = "BadCredentialsException";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(msg));
        // when && then
        Assertions.assertThatThrownBy(() -> authService.login(testUser.getUsername(), wrongPassword))
                .isInstanceOf(BadCredentialsException.class);
    }


    @Test
    @DisplayName("로그아웃 시 기존 토큰 제거 및 블랙리스트 등록")
    void logout_success() {
        // given
        Long userId = testUser.getId();
        String oldRefreshToken = "oldRefreshToken";

        // when
        when(jwtTokenProvider.parseAndValidateToken(oldRefreshToken))
                .thenReturn(claims);
        authService.logout(oldRefreshToken);

        // then
        ArgumentCaptor<BlackList> blackListCaptor = ArgumentCaptor.forClass(BlackList.class);
        verify(refreshTokenRepository, times(1))
                .deleteByToken(oldRefreshToken);
        verify(blackListRepository, times(1))
                .save(blackListCaptor.capture());
        verifyValidateToken(claims);

        Assertions.assertThat(blackListCaptor.getValue().getRefreshJti())
                .isEqualTo(jti);
    }

//    @Test
//    @DisplayName("만료된 토큰으로 로그아웃 시도 시 토큰은 삭제하고 블랙리스트에는 등록하지 않음")
//    void logout_with_expired_token(){
//        // given
//        String expiredRefresh = "invalidRefresh";
//        Claims expiredClaims = Jwts.claims()
//                .expiration(Date.from(Instant.now().minus(10000L, ChronoUnit.MILLIS)))
//                .build();
//        // when
//        when(jwtTokenProvider.parseAndValidateToken(expiredRefresh));
//        authService.logout(testUser.getId(), expiredRefresh);
//
//        // then
//    }

    @Test
    @DisplayName("리프레쉬 토큰으로 토큰 재발급 시," +
            " 엑세스 토큰/리프레쉬 토큰 재발급 및 리프레쉬 토큰 블랙리스트 등록")
    void reissueToken_success() {
        // given
        String oldRefresh = "oldRefreshToken";
        String newAccess = "newAccessToken";
        String newRefresh = "newRefreshToken";
        Long userId = testUser.getId();
        String username = testUser.getUsername();

        // when
        when(jwtTokenProvider.parseAndValidateToken(oldRefresh))
                .thenReturn(claims);
        when(jwtTokenProvider.generateJwtAccessToken(userId, username))
                .thenReturn(newAccess);
        when(jwtTokenProvider.generateJwtRefreshToken(userId, username))
                .thenReturn(newRefresh);
        when(timeProvider.getCurrentInstant()).thenReturn(now);

        TokenPair actual = authService.reissueToken(oldRefresh);

        // then
        ArgumentCaptor<RefreshToken> refreshCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        ArgumentCaptor<BlackList> blackListCaptor = ArgumentCaptor.forClass(BlackList.class);
        verifyValidateToken(claims);
        verify(refreshTokenRepository, times(1))
                .deleteByToken(oldRefresh);
        verify(refreshTokenRepository, times(1))
                .save(refreshCaptor.capture());
        verify(blackListRepository, times(1))
                .save(blackListCaptor.capture());
        Assertions.assertThat(refreshCaptor.getValue().getToken())
                .isEqualTo(newRefresh);
        Assertions.assertThat(blackListCaptor.getValue().getUserId())
                .isEqualTo(userId);
        Assertions.assertThat(actual.access()).isEqualTo(newAccess);
        Assertions.assertThat(actual.refresh()).isEqualTo(newRefresh);

    }

    @Test
    @DisplayName("블랙리스트에 등록된 리프레시토큰으로 토큰 재발급 요청시," +
            "UnauthorizedException")
    void reissueToken_invalid_refresh() throws Exception{
        // given

        // when
        when(jwtTokenProvider.parseAndValidateToken(refreshToken))
                .thenReturn(claims);
        doThrow(UnauthorizedException.class).when(tokenValidator)
                .validateTokenInBlacklist(claims);

        // then
        Assertions.assertThatThrownBy(()->authService.reissueToken(refreshToken))
                .isInstanceOf(UnauthorizedException.class);

    }


    private User createUser(Long userId, String username, String password) {
        User user = User.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_USER)
                .build();
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private void verifyValidateToken(Claims claims) {
        verify(tokenValidator, times(1))
                .validateTokenInBlacklist(claims);
        verify(tokenValidator, times(1))
                .validateTokenType(claims, TokenType.REFRESH_TOKEN);
    }
}
