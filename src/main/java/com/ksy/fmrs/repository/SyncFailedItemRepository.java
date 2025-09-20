package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.player.SyncFailedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncFailedItemRepository extends JpaRepository<SyncFailedItem, Long> {
}
