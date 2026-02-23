package com.ksy.fmrs.exception;

import com.ksy.fmrs.dto.ApiResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotCommentOwnerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotCommentOwner(NotCommentOwnerException e) {
        return error(HttpStatus.FORBIDDEN, "not comment owner", e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException e) {
        return error(HttpStatus.UNAUTHORIZED, "unauthorized", e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(EntityNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, "entity not found", e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, "invalid request", e);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateUsername(DuplicateUsernameException e) {
        return error(HttpStatus.CONFLICT, "username already exists", e);
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidUsername(InvalidUsernameException e) {
        return error(HttpStatus.BAD_REQUEST, "invalid username", e);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPassword(InvalidPasswordException e) {
        return error(HttpStatus.BAD_REQUEST, "invalid password", e);
    }

    @ExceptionHandler(RetiredPlayerException.class)
    public ResponseEntity<ApiResponse<Void>> handleRetiredPlayer(RetiredPlayerException e) {
        return error(HttpStatus.NOT_FOUND, "player is retired", e);
    }

    @ExceptionHandler({NullApiDataException.class, ErrorResponseException.class, EmptyResponseException.class})
    public ResponseEntity<ApiResponse<Void>> handleExternalApi(RuntimeException e) {
        return error(HttpStatus.BAD_GATEWAY, "external api error", e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadJson(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "malformed json request", e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        return error(HttpStatus.FORBIDDEN, "access denied", e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        return error(HttpStatus.BAD_REQUEST, "validation failed", e);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ApiResponse<Void>> handleRequestNotPermitted(RequestNotPermitted e) {
        return error(HttpStatus.TOO_MANY_REQUESTS, "too many requests", e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandled(Exception e) {
        log.error("Unhandled exception", e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, null, "internal server error");
    }

    private ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message, Exception e) {
        log.warn("Handled exception: {}", e.getClass().getSimpleName(), e);
        return ApiResponse.error(status, null, message);
    }
}
