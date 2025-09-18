package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.League;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.dto.SyncReport;
import com.ksy.fmrs.dto.apiFootball.ApiFootballLeague;
import com.ksy.fmrs.repository.SyncFailedItemRepository;
import com.ksy.fmrs.repository.SyncRunRepository;
import com.ksy.fmrs.service.sync.SyncRecordService;
import com.ksy.fmrs.service.sync.SyncRunner;
import com.ksy.fmrs.service.sync.SyncStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.IntStream;

class SyncRunnerTest {

    private SyncRunner syncRunner;

    @Mock private SyncRecordService syncRecordService;
    @BeforeEach
    void setUp() {
        syncRunner = new SyncRunner(syncRecordService);
    }

    private static final int TEST_FIRST_LEAGUE_ID = 1;
    private static final int TEST_LAST_LEAGUE_ID = 10;


    @Test
    @DisplayName("Sync 중 예외 발생 시, failed ++ 하고 작업 재개")
    void Sync_With_Exception() {
        // given && when
        SyncReport report = syncRunner.sync(
                SyncType.LEAGUE,
                IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID).boxed().toList(),
                new testLeagueStrategy()
        );
        // then
        int total = TEST_LAST_LEAGUE_ID - TEST_FIRST_LEAGUE_ID + 1;
        int success = (int) IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID).filter(i -> i % 3 != 0).count();
        checkReport(report, total, success, total - success);
    }


    @Test
    @DisplayName("입력된 Key 값이 없을 시, total=0")
    void Sync_With_Empty_Input() {
        // given && when
        SyncReport report = syncRunner.sync(
                SyncType.LEAGUE,
                IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, -1).boxed().toList(),
                new testLeagueStrategy()
        );

        // then
        checkReport(report, 0, 0, 0);

    }

    @Test
    @DisplayName("특정 키 작업중 예외 발생 시, 해당 키 작업 중지하고 다음 키 작업")
    void requestSportsData_exception() throws Exception {
        // given && when
        failedStrategy failedStrategy = new failedStrategy();
        SyncReport report = syncRunner.sync(
                SyncType.LEAGUE,
                IntStream.rangeClosed(TEST_FIRST_LEAGUE_ID, TEST_LAST_LEAGUE_ID).boxed().toList(),
                failedStrategy
        );

        // then
        int total = TEST_LAST_LEAGUE_ID - TEST_FIRST_LEAGUE_ID + 1;
        checkReport(report, total, 0, total);
        Assertions.assertThat(failedStrategy.getNext()).isEqualTo(0);
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
        public Integer getSyncApiId(Integer key) {return key;}

        @Override
        public List<ApiFootballLeague> requestSportsData(Integer key) {
            if (key % 3 == 0) {
                throw new RuntimeException("test exception");
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

    static final class failedStrategy implements SyncStrategy<Integer, ApiFootballLeague, League> {
        int next = 0;

        @Override
        public Integer getSyncApiId(Integer key) {return key;}

        @Override
        public List<ApiFootballLeague> requestSportsData(Integer key) {
            return List.of();
        }

        @Override
        public void validate(List<ApiFootballLeague> dto) {
            throw new RuntimeException("test exception");
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