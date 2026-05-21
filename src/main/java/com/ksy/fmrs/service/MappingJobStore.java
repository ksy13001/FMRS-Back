package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.MappingJobStatus;
import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import com.ksy.fmrs.dto.MappingJobResponseDto;
import com.ksy.fmrs.exception.DuplicatedMappingJobException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MappingJobStore {

    private final Map<String, MappingJobResponseDto> jobs = new ConcurrentHashMap<>();

    public synchronized MappingJobResponseDto createRunningJobIfAvailable(String type, String strategy, boolean dryRun) {
        if(hasAnnyRunningJob()){
            throw new DuplicatedMappingJobException();
        }

        return createRunningJob(type, strategy, dryRun);
    }

    private Boolean hasAnnyRunningJob() {
        return jobs.values().stream()
                .anyMatch(job->job.status() == MappingJobStatus.RUNNING);
    }

    private MappingJobResponseDto createRunningJob(String type, String strategy,  boolean dryRun) {
        String jobId = UUID.randomUUID().toString();
        MappingJobResponseDto job = MappingJobResponseDto.running(jobId, type, strategy, dryRun, Instant.now());
        jobs.put(jobId, job);
        return job;
    }

    public MappingJobResponseDto complete(String jobId, String strategy, boolean dryRun, FuzzyMappingResponseDto result) {
        MappingJobResponseDto job = getJob(jobId).completed(result, strategy, dryRun,  Instant.now());
        jobs.put(jobId, job);
        return job;
    }

    public MappingJobResponseDto fail(String jobId, String strategy, boolean dryRun, String errorMessage) {
        MappingJobResponseDto job = getJob(jobId).failed(errorMessage, strategy, dryRun, Instant.now());
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
