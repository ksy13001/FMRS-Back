package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.SyncJob;
import com.ksy.fmrs.domain.enums.SyncStatus;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.repository.SyncFailedItemRepository;
import com.ksy.fmrs.repository.SyncJobRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SyncRecordServiceTest {

    @InjectMocks
    private SyncRecordService syncRecordService;
    @Mock
    private SyncJobRepository syncJobRepository;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private SyncFailedItemRepository syncFailedItemRepository;

    private final LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2000, 1, 1, 2, 0, 0);

    @Test
    @DisplayName("SyncType 과 시작 시간을 입력받아서, status=STARTED 인 Sync_job 생성")
    void recordStarted() {
        // given
        SyncType syncType = SyncType.SQUAD;
        given(timeProvider.getCurrentLocalDateTime())
                .willReturn(start);
        // when
        SyncJob actual = syncRecordService.recordStarted(syncType);

        // then
        ArgumentCaptor<SyncJob> syncJob = ArgumentCaptor.forClass(SyncJob.class);
        verify(syncJobRepository, times(1))
                .save(syncJob.capture());
        assertThat(syncJob.getValue().getStatus()).isEqualTo(SyncStatus.STARTED);
        assertThat(syncJob.getValue().getStart()).isEqualTo(start);
        assertThat(syncJob.getValue().getEnd()).isNull();
    }

    @Test
    @DisplayName("syncJob 업데이트 시, SyncJob 없으면 예외발생")
    void recordFinished_Entity_Not_Found() {
        // given
        Long syncJobId = 1L;
        given(syncJobRepository.findById(syncJobId))
                .willReturn(Optional.empty());
        // when && then
        assertThatThrownBy(() -> syncRecordService.recordFinished(syncJobId, 0, 0, 0, 0))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("failed > 0 일경우, status=FAILED 로 업데이트")
    void recordFinished_FAILED() {
        // given
        Long syncJobId = 1L;
        SyncJob syncJob = SyncJob.started(SyncType.SQUAD, start);
        given(syncJobRepository.findById(syncJobId))
                .willReturn(Optional.of(syncJob));
        given(timeProvider.getCurrentLocalDateTime())
                .willReturn(end);
        int total = 10;
        int success = 9;
        int failed = 1;
        int skipped = 0;
        // when
        SyncJob actual = syncRecordService.recordFinished(syncJobId, total, success, failed, skipped);

        // then
        checkUpdatedSyncJob(actual, SyncStatus.FAILED, start, end, total, success, failed);
    }

    @Test
    @DisplayName("failed == 0 일 경우, status=SUCCESS 로 업데이트")
    void recordFinished_STARTED() {
        // given
        Long syncJobId = 1L;
        SyncJob syncJob = SyncJob.started(SyncType.SQUAD, start);
        given(syncJobRepository.findById(syncJobId))
                .willReturn(Optional.of(syncJob));
        given(timeProvider.getCurrentLocalDateTime())
                .willReturn(end);
        int total = 10;
        int success = 10;
        int failed = 0;
        int skipped = 0;
        // when
        SyncJob actual = syncRecordService.recordFinished(syncJobId, total, success, failed, skipped);

        // then
        checkUpdatedSyncJob(actual, SyncStatus.SUCCESS, start, end, total, success, failed);
    }

    private void checkUpdatedSyncJob(SyncJob actual, SyncStatus status, LocalDateTime start, LocalDateTime end, int total, int success, int failed) {
        assertThat(actual.getStatus()).isEqualTo(status);
        assertThat(actual.getStart()).isEqualTo(start);
        assertThat(actual.getEnd()).isEqualTo(end);
        assertThat(actual.getTotal()).isEqualTo(total);
        assertThat(actual.getSuccess()).isEqualTo(success);
        assertThat(actual.getFailed()).isEqualTo(failed);
    }
}