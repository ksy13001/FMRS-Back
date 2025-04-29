package com.ksy.fmrs.util.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class TestTimeProvider implements TimeProvider {

    private final LocalDateTime testTime;

    public TestTimeProvider(LocalDateTime time) {
        this.testTime = time;
    }

    @Override
    public LocalDateTime getCurrentTime() {
        return testTime;
    }
}
