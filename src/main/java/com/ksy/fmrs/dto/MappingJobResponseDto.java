package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.enums.MappingJobStatus;

import java.time.Instant;

public record MappingJobResponseDto(
        String jobId,
        String type,
        MappingJobStatus status,
        Instant startedAt,
        Instant finishedAt,
        FuzzyMappingResponseDto result,
        String errorMessage
) {
    public static MappingJobResponseDto running(String jobId, String type, Instant startedAt) {
        return new MappingJobResponseDto(
                jobId,
                type,
                MappingJobStatus.RUNNING,
                startedAt,
                null,
                null,
                null
        );
    }

    public MappingJobResponseDto completed(FuzzyMappingResponseDto result, Instant finishedAt) {
        return new MappingJobResponseDto(
                jobId,
                type,
                MappingJobStatus.COMPLETED,
                startedAt,
                finishedAt,
                result,
                null
        );
    }

    public MappingJobResponseDto failed(String errorMessage, Instant finishedAt) {
        return new MappingJobResponseDto(
                jobId,
                type,
                MappingJobStatus.FAILED,
                startedAt,
                finishedAt,
                result,
                errorMessage
        );
    }
}
