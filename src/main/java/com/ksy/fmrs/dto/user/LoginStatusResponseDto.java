package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class LoginStatusResponseDto {
    private boolean success;
    private String message;
    private UserDetailsDto dto;

    public static ResponseEntity<LoginStatusResponseDto> authenticated(Long userId, String userName) {
        LoginStatusResponseDto dto = new LoginStatusResponseDto();
        dto.success = true;
        dto.message = "Authenticated";
        dto.dto = new UserDetailsDto(userId, userName);
        return ResponseEntity.ok().body(dto);
    }

}
