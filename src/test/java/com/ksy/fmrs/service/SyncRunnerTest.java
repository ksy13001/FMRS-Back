package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.SyncJob;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.dto.SyncReport;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.service.sync.SyncRecordService;
import com.ksy.fmrs.service.sync.SyncRunner;
import com.ksy.fmrs.service.sync.SyncStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncRunnerTest {

    private SyncRunner syncRunner;
    private SyncJob syncJob;
    private static final LocalDateTime START_TIME = LocalDateTime
            .of(2000, 8, 14, 0, 0, 0);
    private static final String REQUEST_ERROR_MESSAGE = "requestSportsData exception";
    @Mock
    private SyncRecordService syncRecordService;

    @BeforeEach
    void setUp() {
        syncRunner = new SyncRunner(syncRecordService);
        syncJob = SyncJob.started(SyncType.LEAGUE, START_TIME);

    }

    private static final int TEST_FIRST_LEAGUE_ID = 1;
    private static final int TEST_LAST_LEAGUE_ID = 10;


    @Test
    @DisplayName("Sync 중 예외 발생 시, 실패한 아이템 저장하고 작업 재개")
    void Sync_With_Exception() {
        // given
        given(syncRecordService.recordStarted(SyncType.LEAGUE))
                .willReturn(syncJob);

        // when
        SyncReport report = syncRunner.sync(
                IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID).boxed().toList(),
                new testLeagueStrategy()
        );
        // then
        int total = TEST_LAST_LEAGUE_ID - TEST_FIRST_LEAGUE_ID + 1;
        int success = (int) IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID).filter(i -> i % 3 != 0).count();
        checkReport(report, total, success, total - success);
        IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID)
                .filter(i->i % 3 == 0)
                .forEach(apiId->{
                    verify(syncRecordService, times(1))
                            .recordFailedItem(
                                    SyncType.LEAGUE,
                                    apiId,
                                    REQUEST_ERROR_MESSAGE,
                                    "RuntimeException",
                                    syncJob);
                }
                );
    }

    @Test
    @DisplayName("입력된 Key 값이 없을 시, total=0")
    void Sync_With_Empty_Input() {
        // given
        given(syncRecordService.recordStarted(SyncType.LEAGUE))
                .willReturn(syncJob);

        // when
        SyncReport report = syncRunner.sync(
                IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, -1).boxed().toList(),
                new testLeagueStrategy()
        );

        // then
        checkReport(report, 0, 0, 0);
        verify(syncRecordService, never())
                .recordFailedItem(any(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("모든 작업 성공")
    void requestSportsData_exception() {
        // given
        given(syncRecordService.recordStarted(SyncType.LEAGUE))
                .willReturn(syncJob);
        // when
        allPassStrategy allPassStrategy = new allPassStrategy();
        SyncReport report = syncRunner.sync(
                IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID).boxed().toList(),
                allPassStrategy
        );

        // then
        int total = TEST_LAST_LEAGUE_ID - TEST_FIRST_LEAGUE_ID + 1;
        checkReport(report, total, total, 0);
        Assertions.assertThat(allPassStrategy.getNext()).isEqualTo(total);

    }

    private void checkReport(SyncReport report, int total, int success, int failed) {
        Assertions.assertThat(report.getTotal())
                .isEqualTo(total);
        Assertions.assertThat(report.getSuccess())
                .isEqualTo(success);
        Assertions.assertThat(report.getFailed())
                .isEqualTo(failed);
    }

    static final class testLeagueStrategy implements SyncStrategy<Integer, ApiFootballLeague, League> {
        @Override
        public SyncType getSyncType() {
            return SyncType.LEAGUE;
        }

        @Override
        public Integer getSyncApiId(Integer key) {return key;}

        @Override
        public List<ApiFootballLeague> requestSportsData(Integer key) {
            if (key % 3 == 0) {
                throw new RuntimeException(REQUEST_ERROR_MESSAGE);
            }
            return List.of();
        }

        @Override
        public void validate(List<ApiFootballLeague> dto) {

        }

        @Override
        public List<League> transformToTarget(List<ApiFootballLeague> dto) {
            return List.of();
        }

        @Override
        public void persist(List<League> entities, Integer key) {

        }
    }

    static final class allPassStrategy implements SyncStrategy<Integer, ApiFootballLeague, League> {
        int next = 0;

        @Override
        public SyncType getSyncType() {
            return SyncType.LEAGUE;
        }

        @Override
        public Integer getSyncApiId(Integer key) {return key;}

        @Override
        public List<ApiFootballLeague> requestSportsData(Integer key) {
            return List.of();
        }

        @Override
        public void validate(List<ApiFootballLeague> dto) {
        }

        @Override
        public List<League> transformToTarget(List<ApiFootballLeague> dto) {
            next++;
            return List.of();
        }

        @Override
        public void persist(List<League> entities, Integer key) {
        }

        public int getNext() {
            return this.next;
        }
    }
}