package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import com.ksy.fmrs.dto.MappingJobResponseDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MappingJobStore {

    private final Map<String, MappingJobResponseDto> jobs = new ConcurrentHashMap<>();

    public MappingJobResponseDto createRunningJob(String type) {
        String jobId = UUID.randomUUID().toString();
        MappingJobResponseDto job = MappingJobResponseDto.running(jobId, type, Instant.now());
        jobs.put(jobId, job);
        return job;
    }

    public MappingJobResponseDto complete(String jobId, FuzzyMappingResponseDto result) {
        MappingJobResponseDto job = getJob(jobId).completed(result, Instant.now());
        jobs.put(jobId, job);
        return job;
    }

    public MappingJobResponseDto fail(String jobId, String errorMessage) {
        MappingJobResponseDto job = getJob(jobId).failed(errorMessage, Instant.now());
        jobs.put(jobId, job);
        return job;
    }

    public MappingJobResponseDto getJob(String jobId) {
        MappingJobResponseDto job = jobs.get(jobId);

        if (job == null) {
            throw new IllegalArgumentException("mapping job not found: " + jobId);
        }

        return job;
    }
}
