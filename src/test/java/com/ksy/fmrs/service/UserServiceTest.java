package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.enums.Role;
import com.ksy.fmrs.dto.user.SignupRequestDto;
import com.ksy.fmrs.dto.user.SignupResponseDto;
import com.ksy.fmrs.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("사용자명이 존재하면 findByUsername 은 User 를 반환한다")
    void findByUsername_success() {
        // given
        String username = "testUser";
        User user = buildUser(1L, username, "encodedPw", Role.ROLE_USER);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // when
        User actual = userService.findByUsername(username);

        // then
        Assertions.assertThat(actual).isSameAs(user);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("존재하지 않는 사용자명으로 조회 시 EntityNotFoundException")
    void findByUsername_notFound() {
        // given
        String username = "missing";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> userService.findByUsername(username))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("이미 존재하는 사용자명으로 가입 요청 시 409 반환")
    void createUser_duplicatedUsername() {
        // given
        SignupRequestDto request = new SignupRequestDto("duplicate", "Valid@1234");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // when
        ResponseEntity<SignupResponseDto> response = userService.createUser(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().isSuccess()).isFalse();
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("검증에 실패하면 400 반환 및 저장하지 않음")
    void createUser_validationFail() {
        // given
        SignupRequestDto request = new SignupRequestDto("a", "short");
        when(userRepository.existsByUsername(request.username())).thenReturn(false);

        // when
        ResponseEntity<SignupResponseDto> response = userService.createUser(request);

        // then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().isSuccess()).isFalse();
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("검증 통과 시 비밀번호를 인코딩하여 저장하고 200 반환")
    void createUser_success() {
        // given
        SignupRequestDto request = new SignupRequestDto("Valid_User1", "Valid@1234");
        String encodedPassword = "encodedPassword";
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User toSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(toSave, "id", 1L);
            return toSave;
        });

        // when
        ResponseEntity<SignupResponseDto> response = userService.createUser(request);

        // then
        SignupResponseDto body = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body.isSuccess()).isTrue();
        Assertions.assertThat(body.getUserId()).isEqualTo(1L);
        verify(passwordEncoder, times(1)).encode(request.password());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        Assertions.assertThat(savedUser.getUsername()).isEqualTo(request.username());
        Assertions.assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        Assertions.assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
    }

    private User buildUser(Long id, String username, String password, Role role) {
        User user = User.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
