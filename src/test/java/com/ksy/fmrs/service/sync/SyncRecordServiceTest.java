package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.repository.SyncFailedItemRepository;
import com.ksy.fmrs.repository.SyncJobRepository;
import com.ksy.fmrs.util.time.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncRecordServiceTest {

    @InjectMocks
    private SyncRecordService syncRecordService;

    @Mock
    private SyncJobRepository syncJobRepository;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private SyncFailedItemRepository  syncFailedItemRepository;

    @Test
    @DisplayName("테스트설명")
    void 메서드명() throws Exception{
        // given

        // when

        // then
    }
}