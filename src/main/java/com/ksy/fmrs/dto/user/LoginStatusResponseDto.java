package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginStatusResponseDto {
    private boolean authenticated;
    private UserDetailsDto user;

    public static LoginStatusResponseDto authenticated(Long userId, String userName) {
        LoginStatusResponseDto dto = new LoginStatusResponseDto();
        dto.authenticated = true;
        dto.user = new UserDetailsDto(userId, userName);
        return dto;
    }

    public static LoginStatusResponseDto unauthenticated() {
        LoginStatusResponseDto dto = new LoginStatusResponseDto();
        dto.authenticated = false;
        return dto;
    }

}
