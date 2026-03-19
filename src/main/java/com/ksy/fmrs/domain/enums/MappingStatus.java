package com.ksy.fmrs.domain.enums;

import lombok.Getter;

/**
 *
 * UNMAPPED   → 매핑 시도 전
 * MATCHED    → 매핑 성공
 * FAILED     → 필수 데이터 누락으로 자동 매핑 불가
 * NO_MATCH   → 매핑 시도했지만 후보 없음
 * DUPLICATE  → 후보는 있으나 distinct fm_uid 가 2개 이상이라 자동 확정 불가
 *
 * */

@Getter
public enum MappingStatus {
    FAILED("FAILED"),
    MATCHED("MATCHED"),
    UNMAPPED("UNMAPPED"),
    DUPLICATE("DUPLICATE"),
    NO_MATCH("NO_MATCH");

    private final String value;

    MappingStatus(String value) {
        this.value = value;
    }
}
