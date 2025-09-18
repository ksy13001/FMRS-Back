package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.SyncJob;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.domain.player.SyncFailedItem;
import com.ksy.fmrs.repository.SyncFailedItemRepository;
import com.ksy.fmrs.repository.SyncRunRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class SyncRecordService {
    private final SyncRunRepository syncRunRepository;
    private final SyncFailedItemRepository syncFailedItemRepository;
    private final TimeProvider timeProvider;

    @Transactional
    public SyncJob recordStarted(SyncType syncType){
        LocalDateTime start = timeProvider.getCurrentLocalDateTime();
        return syncRunRepository.save(SyncJob.started(syncType, start));
    }

    @Transactional
    public void recordFinished(SyncType syncType, LocalDateTime start, int total, int success, int failed){
        if (failed > 0) {
            syncRunRepository.save(SyncJob.failed(syncType, start, timeProvider.getCurrentLocalDateTime(), total, success, failed));
            return;
        }
        syncRunRepository.save(SyncJob.success(syncType, start, timeProvider.getCurrentLocalDateTime(), total, success, failed));
    }

    @Transactional
    public void recordFailedItem(SyncType syncType, Integer apiId, String errorMessage, String errorCode, SyncJob syncJob){
        syncFailedItemRepository.save(new SyncFailedItem(syncType, apiId, errorMessage, errorCode, syncJob));
    }
}
