package com.ksy.fmrs.service;

import com.ksy.fmrs.dto.ValidateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class SyncTemplate {

    public <K, D, E> void sync(Iterable<K> keys, SyncCallback<K, D, E> callback) {
        int success = 0; int failed = 0;
        for (K key : keys) {
            try {
                callback.beforeEach(key);
                List<D> dto = callback.requestSportsData(key);
                callback.validate(dto);
                List<E> entity = callback.toEntity(dto);
                callback.persist(entity, key);
                success++;
            } catch (Exception e){
                log.error("Sync failed: {}", e.getMessage());
                failed++;
            }
            callback.afterEach(key);
        }
        log.info("Sync result: success: {}, failed: {}", success, failed);
    }
}
