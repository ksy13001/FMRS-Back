package com.ksy.fmrs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private Instant timestamp;

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, data, message, Instant.now()));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data, message, Instant.now()));
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, T data, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, data, message, Instant.now()));
    }
}
