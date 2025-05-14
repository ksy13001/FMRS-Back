package com.ksy.fmrs.util.time;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestTimeProvider implements TimeProvider {

    private final LocalDateTime testTime;
    private final LocalDate testLocalDate;

    public TestTimeProvider(LocalDateTime time, LocalDate testLocalDate) {
        this.testTime = time;
        this.testLocalDate = testLocalDate;
    }

    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return testTime;
    }

    @Override
    public LocalDate getCurrentLocalDate() {
        return testLocalDate;
    }
}
