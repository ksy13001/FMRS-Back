package com.ksy.fmrs.dto.user;

public record TokenPairWithId(Long userId, String access, String refresh) {
}
