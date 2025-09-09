package com.ksy.fmrs.service;

import java.util.List;

public interface SyncCallback<K , D, E>{
    void beforeEach(K key);
    List<D> requestSportsData(K key);
    void validate(List<D> dto);
    List<E> toEntity(List<D> dto);
    void persist(List<E> entities, K key);
    void afterEach(K key);
}
