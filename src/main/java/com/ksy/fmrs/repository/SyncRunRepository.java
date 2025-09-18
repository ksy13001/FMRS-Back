package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncRunRepository extends JpaRepository<SyncJob, Long> {

}
