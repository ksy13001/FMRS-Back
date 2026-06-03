package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.enums.MappingJobStatus;

import java.time.Instant;

public record MappingJobResponseDto(
        String jobId,
        String type,
        MappingJobStatus status,
        String strategy,
        boolean dryRun,
        Instant startedAt,
        Instant finishedAt,
        MappingJobResult result,
        String errorMessage
) {
    public static MappingJobResponseDto running(String jobId, String type, String strategy, boolean dryRun, Instant startedAt) {
        return new MappingJobResponseDto(
                jobId,
                type,
                MappingJobStatus.RUNNING,
                strategy,
                dryRun,
                startedAt,
                null,
                null,
                null
        );
    }

    public MappingJobResponseDto completed(MappingJobResult result, String strategy, boolean dryRun, Instant finishedAt) {
        return new MappingJobResponseDto(
                jobId,
                type,
                MappingJobStatus.COMPLETED,
                strategy,
                dryRun,
                startedAt,
                finishedAt,
                result,
                null
        );
    }

    public MappingJobResponseDto failed(String errorMessage, String strategy, boolean dryRun, Instant finishedAt) {
        return new MappingJobResponseDto(
                jobId,
                type,
                MappingJobStatus.FAILED,
                strategy,
                dryRun,
                startedAt,
                finishedAt,
                result,
                errorMessage
        );
    }
}
