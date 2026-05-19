package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.FuzzyStrategy;
import com.ksy.fmrs.dto.FuzzyMappingRequestDto;
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

    public MappingJobResponseDto startFuzzyMappingJob(FuzzyStrategy strategy, boolean dryRun) {
        MappingJobResponseDto job = mappingJobStore.createRunningJob(FUZZY_MAPPING_JOB_TYPE, strategy.name(), dryRun);
        log.info("[mapping-job:{} fuzzyStrategy:{} dryRun:{} ] fuzzy mapping job submitted",
                job.jobId(), strategy.name(), dryRun);

        fuzzyMappingJobRunner.runAsync(job.jobId(), strategy, dryRun);
        return job;
    }

    public MappingJobResponseDto getJob(String jobId) {
        return mappingJobStore.getJob(jobId);
    }
}
