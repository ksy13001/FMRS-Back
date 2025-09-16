package com.ksy.fmrs.service;

import java.util.List;

public interface SyncStrategy<K, D, T>{
    List<D> requestSportsData(K key);
    void validate(List<D> dto);
    List<T> transformToTarget(List<D> dto);
    void persist(List<T> entities, K key);
}
