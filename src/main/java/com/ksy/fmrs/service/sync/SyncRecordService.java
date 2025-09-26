package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.SyncJob;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.domain.player.SyncFailedItem;
import com.ksy.fmrs.repository.SyncFailedItemRepository;
import com.ksy.fmrs.repository.SyncJobRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class SyncRecordService {
    private final SyncJobRepository syncJobRepository;
    private final SyncFailedItemRepository syncFailedItemRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public SyncJob recordStarted(SyncType syncType){
        return syncJobRepository.save(SyncJob.started(syncType, timeProvider.getCurrentLocalDateTime()));
    }

    @Transactional
    public SyncJob recordFinished(Long syncJobId, int total, int success, int failed, int skipped){
        SyncJob syncJob = syncJobRepository.findById(syncJobId)
                .orElseThrow(()->new EntityNotFoundException("Sync Job Not Started"));
        if (failed > 0) {
            syncJob.failed(timeProvider.getCurrentLocalDateTime(), total, success, failed, skipped);
            return syncJob;
        }
        syncJob.success(timeProvider.getCurrentLocalDateTime(), total, success, failed, skipped);
        return syncJob;
    }

    @Transactional
    public void recordFailedItem(SyncType syncType, Integer apiId, String errorMessage, String errorCode, SyncJob syncJob){
        syncFailedItemRepository.save(new SyncFailedItem(syncType, apiId, errorMessage, errorCode, syncJob));
    }
}
