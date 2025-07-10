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

    public static ResponseEntity<SignupResponseDto> success(Long userId) {
        SignupResponseDto dto = new SignupResponseDto();
        dto.setSuccess(Boolean.TRUE);
        dto.setMessage("user created successfully");
        dto.setUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    public static ResponseEntity<SignupResponseDto> validationFail() {
        SignupResponseDto dto = new SignupResponseDto();
        dto.setSuccess(false);
        dto.setMessage("validate failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    public static ResponseEntity<SignupResponseDto> duplicated() {
        SignupResponseDto dto = new SignupResponseDto();
        dto.setSuccess(false);
        dto.setMessage("Username or email already exists");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
    }

}
