package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.enums.Role;
import com.ksy.fmrs.dto.user.SignupRequestDto;
import com.ksy.fmrs.dto.user.SignupResponseDto;
import com.ksy.fmrs.exception.DuplicateUsernameException;
import com.ksy.fmrs.exception.InvalidPasswordException;
import com.ksy.fmrs.exception.InvalidUsernameException;
import com.ksy.fmrs.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("user not found"+username));
    }

    @Transactional
    public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {
        if (existsByUsername(signupRequestDto.username())) {
            throw new DuplicateUsernameException("duplicate username:"+signupRequestDto.username());
        }

        if (!validateUsername(signupRequestDto.username())){
            throw new InvalidUsernameException("invalid username");
        }

        if(!validatePassword(signupRequestDto.password())){
            throw new InvalidPasswordException("invalid password");
        }

        User user = userRepository.save(User.builder()
                        .username(signupRequestDto.username())
                        .password(passwordEncoder.encode(signupRequestDto.password()))
                        .role(Role.ROLE_USER)
                        .build()
        );
        return SignupResponseDto.success(user.getId());
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 2~20 글자,  소문자(a-z), 대문자(A-Z), 숫자(0-9), 대시(-), 밑줄(_), 아포스트로피('), 마침표(.)를 포함
    private boolean validateUsername(String username) {
        return username.matches("^[a-zA-Z0-9\\-_'\\.]{2,20}$");
    }

    // 8~64 글자
    private boolean validatePassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60\\x7B-\\x7E])" +
                "[A-Za-z\\d\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60\\x7B-\\x7E]{8,64}$");
    }
}
