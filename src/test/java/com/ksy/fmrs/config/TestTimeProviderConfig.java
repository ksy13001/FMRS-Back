package com.ksy.fmrs.config;

import com.ksy.fmrs.util.time.TestTimeProvider;
import com.ksy.fmrs.util.time.TimeProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TestConfiguration
public class TestTimeProviderConfig {

    @Bean
    public TimeProvider timeProvider() {
        return new TestTimeProvider(
                LocalDateTime.of(2000, 8, 14, 0, 0),
                LocalDate.of(2000, 8, 14),
                Date.valueOf(LocalDate.of(2000, 8, 14)),
                Instant.EPOCH
        );
    }
}
