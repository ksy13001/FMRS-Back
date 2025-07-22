package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDetailsDto {
    private Long userId;
    private String userName;

    public UserDetailsDto(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
