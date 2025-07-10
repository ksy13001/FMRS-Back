package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.user.SignupRequestDto;
import com.ksy.fmrs.dto.user.SignupResponseDto;
import com.ksy.fmrs.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<SignupResponseDto> signUp(@RequestBody SignupRequestDto dto) {
        return userService.createUser(dto);
    }
}
