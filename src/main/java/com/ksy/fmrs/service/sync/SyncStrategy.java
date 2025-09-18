package com.ksy.fmrs.service.sync;

import java.util.List;

public interface SyncStrategy<K, D, T>{
    Integer getSyncApiId(K key);
    List<D> requestSportsData(K key);
    void validate(List<D> dto);
    List<T> transformToTarget(List<D> dto);
    void persist(List<T> entities, K key);
}
