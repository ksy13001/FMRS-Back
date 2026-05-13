package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.MappingJobResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MappingJobService {

    private static final String FUZZY_MAPPING_JOB_TYPE = "FUZZY_MAPPING";

    private final MappingJobStore mappingJobStore;
    private final FuzzyMappingJobRunner fuzzyMappingJobRunner;

    public MappingJobResponseDto startFuzzyMappingJob() {
        MappingJobResponseDto job = mappingJobStore.createRunningJob(FUZZY_MAPPING_JOB_TYPE);
        log.info("[mapping-job:{}] fuzzy mapping job submitted", job.jobId());
        fuzzyMappingJobRunner.runAsync(job.jobId());
        return job;
    }

    public MappingJobResponseDto getJob(String jobId) {
        return mappingJobStore.getJob(jobId);
    }
}
