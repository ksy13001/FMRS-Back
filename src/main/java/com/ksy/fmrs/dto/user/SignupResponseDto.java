package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class SignupResponseDto {

    private boolean success;
    private String message;
    private Long userId;

    public static SignupResponseDto success(Long userId) {
        SignupResponseDto dto = new SignupResponseDto();
        dto.setSuccess(Boolean.TRUE);
        dto.setMessage("user created successfully");
        dto.setUserId(userId);
        return dto;
    }
}
