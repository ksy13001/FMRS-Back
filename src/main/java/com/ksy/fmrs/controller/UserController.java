package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.ApiResponse;
import com.ksy.fmrs.dto.user.SignupRequestDto;
import com.ksy.fmrs.dto.user.SignupResponseDto;
import com.ksy.fmrs.service.UserService;
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
    public ResponseEntity<ApiResponse<SignupResponseDto>> signUp(@RequestBody SignupRequestDto dto) {
        return ApiResponse.ok(userService.createUser(dto), "user sign up success");
    }
}
