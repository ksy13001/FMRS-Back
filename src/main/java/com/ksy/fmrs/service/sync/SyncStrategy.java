package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.enums.SyncType;

import java.util.List;

public interface SyncStrategy<K, D, T>{
    SyncType getSyncType();
    Integer getSyncApiId(K key);
    List<D> requestSportsData(K key);
    void validate(List<D> dto);
    List<T> transformToTarget(List<D> dto);
    void persist(List<T> entities, K key);
}
