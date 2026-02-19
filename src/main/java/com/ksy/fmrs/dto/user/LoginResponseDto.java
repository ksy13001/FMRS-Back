package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private Long userId;
    private String username;

    public static LoginResponseDto success(Long userId, String username) {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setUsername(username);
        dto.setUserId(userId);
        return dto;
    }
}
