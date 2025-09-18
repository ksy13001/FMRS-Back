package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.SyncJob;
import com.ksy.fmrs.domain.enums.SyncType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "sync_failed_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class SyncFailedItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer apiId;

    @Enumerated(EnumType.STRING)
    private SyncType type;

    private String errorMessage;

    private String errorCode;

    @JoinColumn(name = "job_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SyncJob syncJob;

    @Builder
    public SyncFailedItem(SyncType type, Integer apiId, String errorMessage, String errorCode, SyncJob syncJob) {
        this.type = type;
        this.apiId = apiId;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.syncJob = syncJob;
    }
}
