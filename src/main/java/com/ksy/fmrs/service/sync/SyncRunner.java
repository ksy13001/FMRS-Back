package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.SyncJob;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.dto.SyncReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SyncRunner {

    private final SyncRecordService syncRecordService;

    public <K, D, T> SyncReport sync(Iterable<K> keys, SyncStrategy<K, D, T> strategy) {
        int total = 0;
        int success = 0;
        int failed = 0;
        SyncType type = strategy.getSyncType();
        SyncJob syncJob = syncRecordService.recordStarted(type);
        for (K key : keys) {
            total++;
            Integer apiId = null;
            try {
                apiId = strategy.getSyncApiId(key);
                List<D> data = strategy.requestSportsData(key);
                strategy.validate(data);
                List<T> target = strategy.transformToTarget(data);
                strategy.persist(target, key);
                success++;
            } catch (Exception e) {
                log.error("Sync failed: {}", e.getMessage());
                failed++;
                syncRecordService.recordFailedItem(type, apiId, e.getMessage(), e.getClass().getSimpleName(), syncJob);
            }
        }
        log.info("Sync result: success: {}, failed: {}", success, failed);
        syncRecordService.recordFinished(syncJob.getId(), total, success, failed);
        return new SyncReport(total, success, failed);
    }
}
