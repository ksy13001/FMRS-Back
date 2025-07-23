package com.ksy.fmrs.repository.user;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.config.TestTimeProviderConfig;
import com.ksy.fmrs.domain.BlackList;
import com.ksy.fmrs.repository.BlackListRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Import({TestQueryDSLConfig.class, TestTimeProviderConfig.class})
@DataJpaTest
class BlackListRepositoryTest {

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private TestEntityManager tem;

    @Test
    @DisplayName("테스트설명")
    void 메서드명() throws Exception {
        // given
        Instant now = Instant.now();
        int total = 100000;
        int batch = 1000;

        for (int i = 0; i < total; i += batch) {
            List<BlackList> blackLists = IntStream.range(i, i + batch)
                    .mapToObj( offset ->
                        createBlackList(
                                (long)offset,
                                UUID.randomUUID().toString(),
                                now.minus(100, ChronoUnit.MILLIS))
                    ).toList();
            blackListRepository.saveAll(blackLists);
            tem.flush();
            tem.clear();
        }

        // when
        long start = System.nanoTime();
        blackListRepository.deleteAllByExpiryDateBefore(Instant.now());
        long end = System.nanoTime();
        tem.flush();
        tem.clear();

        // then
        long elapsedMs = (end - start) / 1_000_000;
        System.out.printf("10만 건 삭제 소요 시간: %d ms%n", elapsedMs);
    }

    private BlackList createBlackList(Long userId, String jti, Instant expiryDate) {
        return BlackList.builder()
                .userId(userId)
                .refreshJti(jti)
                .expiryDate(expiryDate)
                .build();
    }
}