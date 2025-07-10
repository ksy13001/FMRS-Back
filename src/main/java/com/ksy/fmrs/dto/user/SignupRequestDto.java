package com.ksy.fmrs.dto.user;

import org.springframework.lang.NonNull;

public record SignupRequestDto(
        @NonNull
        String username,
        @NonNull
        String password) {
}
