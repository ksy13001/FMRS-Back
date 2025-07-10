package com.ksy.fmrs.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ReissueResponseDto {
    private boolean success;
    private String message;

    public static ResponseEntity<ReissueResponseDto> success() {
        ReissueResponseDto dto = new ReissueResponseDto();
        dto.success = true;
        dto.message = "Token refreshed successfully!";
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    public static ResponseEntity<ReissueResponseDto> invalidHeader() {
        ReissueResponseDto dto = new ReissueResponseDto();
        dto.success = false;
        dto.message = "Invalid header";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    public static ResponseEntity<ReissueResponseDto> expiredToken() {
        ReissueResponseDto dto = new ReissueResponseDto();
        dto.success = false;
        dto.message = "Session expired. Please login again.";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
    }

    public static ResponseEntity<ReissueResponseDto> serverError() {
        ReissueResponseDto dto = new ReissueResponseDto();
        dto.success = false;
        dto.message = "Connection timeout";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }

    public static ResponseEntity<ReissueResponseDto> blacklistedRefreshToken() {
        ReissueResponseDto dto = new ReissueResponseDto();
        dto.success = false;
        dto.message = "Already logged out";
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dto);
    }
}
