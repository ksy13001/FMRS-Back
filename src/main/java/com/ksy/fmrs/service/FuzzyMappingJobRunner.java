package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FuzzyMappingJobRunner {

    private final MappingService mappingService;
    private final MappingJobStore mappingJobStore;

    @Async
    public void runAsync(String jobId) {
        log.info("[mapping-job:{}] fuzzy mapping started", jobId);

        try {
            FuzzyMappingResponseDto result = mappingService.matchFuzzy(jobId);
            mappingJobStore.complete(jobId, result);
            log.info("[mapping-job:{}] fuzzy mapping completed: {}", jobId, result);
        } catch (Throwable e) {
            mappingJobStore.fail(jobId, e.getMessage());
            log.error("[mapping-job:{}] fuzzy mapping failed", jobId, e);

            if (e instanceof Error error) {
                throw error;
            }
        }
    }
}
