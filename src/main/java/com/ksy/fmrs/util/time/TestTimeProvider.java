package com.ksy.fmrs.util.time;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;


public class TestTimeProvider implements TimeProvider {

    private final LocalDateTime testTime;
    private final LocalDate testLocalDate;
    private final Date testDate;
    private final Instant testInstant;

    public TestTimeProvider(LocalDateTime time, LocalDate testLocalDate, Date testDate, Instant testInstant) {
        this.testTime = time;
        this.testLocalDate = testLocalDate;
        this.testDate = testDate;
        this.testInstant = testInstant;
    }

    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return testTime;
    }

    @Override
    public LocalDate getCurrentLocalDate() {
        return testLocalDate;
    }

    @Override
    public Date getCurrentDate(){
        return testDate;
    }

    @Override
    public Instant getCurrentInstant() {
        return testInstant;
    }
}
