package com.ksy.fmrs.domain;

import com.ksy.fmrs.domain.enums.SyncStatus;
import com.ksy.fmrs.domain.enums.SyncType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "sync_job")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class SyncJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SyncType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SyncStatus status;

    private LocalDateTime start;
    private LocalDateTime end;

    @Column(nullable = false)
    private int total;
    @Column(nullable = false)
    private int success;
    @Column(nullable = false)
    private int failed;

    public SyncJob(SyncType type,
                   SyncStatus status,
                   LocalDateTime startTime,
                   LocalDateTime endTime,
                   int total,
                   int success,
                   int failed) {
        this.type = type;
        this.status = status;
        this.start = startTime;
        this.end = endTime;
        this.total = total;
        this.success = success;
        this.failed = failed;
    }

    public static SyncJob started(SyncType type, LocalDateTime start){
        return new SyncJob(type, SyncStatus.STARTED, start, null, 0, 0, 0);
    }

    public void success(LocalDateTime end, int total, int success, int failed){
        this.end = end;
        this.total = total;
        this.success = success;
        this.failed = failed;
        this.status = SyncStatus.SUCCESS;
    }

    public void failed(LocalDateTime end, int total, int success, int failed){
        this.end = end;
        this.total = total;
        this.success = success;
        this.failed = failed;
        this.status = SyncStatus.FAILED;
    }

}
