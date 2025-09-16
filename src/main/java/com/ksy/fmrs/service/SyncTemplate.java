package com.ksy.fmrs.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SyncTemplate {

    public <K, D, T> void sync(Iterable<K> keys, SyncCallback<K, D, T> callback) {
        int success = 0; int failed = 0;
        for (K key : keys) {
            try {
                callback.beforeEach(key);
                List<D> dto = callback.requestSportsData(key);
                callback.validate(dto);
                List<T> target = callback.transformToTarget(dto);
                callback.persist(target, key);
                success++;
            } catch (Exception e){
                log.error("Sync failed: {}", e.getMessage());
                failed++;
            }
        }
        log.info("Sync result: success: {}, failed: {}", success, failed);
    }
}
