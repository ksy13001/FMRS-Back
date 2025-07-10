package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class LoginResponseDto {
    private boolean success;
    private String message;
    private Long userId;
    private String username;

    public static ResponseEntity<LoginResponseDto> success(Long userId, String username) {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setSuccess(true);
        dto.setMessage("Login successful");
        dto.setUsername(username);
        dto.setUserId(userId);
        return ResponseEntity.ok(dto);
    }

    public static ResponseEntity<LoginResponseDto> authenticationFailed() {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setSuccess(false);
        dto.setMessage("Invalid email or password. Please try again.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
    }
}
