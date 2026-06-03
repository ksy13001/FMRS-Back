package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.ExactMappingPass;
import com.ksy.fmrs.domain.enums.FuzzyStrategy;
import com.ksy.fmrs.dto.MappingJobResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MappingJobService {

    private static final String FUZZY_MAPPING_JOB_TYPE = "FUZZY_MAPPING";
    private final MappingJobStore mappingJobStore;
    private final FuzzyMappingJobRunner fuzzyMappingJobRunner;
    private final ExactMappingJobRunner exactMappingJobRunner;

    public MappingJobResponseDto startFuzzyMappingJob(FuzzyStrategy strategy, boolean dryRun) {

        MappingJobResponseDto job = mappingJobStore.createRunningJobIfAvailable(FUZZY_MAPPING_JOB_TYPE, strategy.name(), dryRun);
        log.info("[mapping-job:{} fuzzyStrategy:{} dryRun:{} ] fuzzy mapping job submitted",
                job.jobId(), strategy.name(), dryRun);

        fuzzyMappingJobRunner.runAsync(job.jobId(), strategy, dryRun);
        return job;
    }

    public MappingJobResponseDto startExact4KeyMappingJob(boolean dryRun) {
        return startExactMappingJob(ExactMappingPass.FOUR_KEY, dryRun);
    }

    public MappingJobResponseDto startTokenNameExactMappingJob(boolean dryRun) {
        return startExactMappingJob(ExactMappingPass.TOKEN_NAME, dryRun);
    }

    public MappingJobResponseDto startFirstNameTokenAndFirstLastNameTokenExactMappingJob(boolean dryRun) {
        return startExactMappingJob(ExactMappingPass.FIRST_NAME_TOKEN_AND_FIRST_LAST_NAME_TOKEN, dryRun);
    }

    private MappingJobResponseDto startExactMappingJob(ExactMappingPass pass, boolean dryRun) {
        MappingJobResponseDto job = mappingJobStore.createRunningJobIfAvailable(
                pass.getJobType(),
                pass.getStrategy(),
                dryRun
        );
        log.info("[mapping-job:{} pass:{} dryRun:{}] exact mapping job submitted", job.jobId(), pass, dryRun);

        exactMappingJobRunner.runAsync(job.jobId(), pass, dryRun);
        return job;
    }

    public MappingJobResponseDto getJob(String jobId) {
        return mappingJobStore.getJob(jobId);
    }

    public Optional<MappingJobResponseDto> getCurrentMappingJob() {
        return mappingJobStore.getCurrentJob();
    }

    public List<MappingJobResponseDto> getAllMappingJobs() {
        return  mappingJobStore.getAllJobs();
    }
}
