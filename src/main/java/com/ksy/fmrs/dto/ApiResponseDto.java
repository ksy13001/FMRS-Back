package com.ksy.fmrs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponseDto<T> {
    private Boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    public static <T> ApiResponseDto<T> ok(T data,  String message) {
        return new ApiResponseDto<>(true, data, message, Instant.now());
    }
}
