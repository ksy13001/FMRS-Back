package com.ksy.fmrs.util.time;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Primary
@Component
public class SystemTimeProvider implements TimeProvider {

    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    @Override
    public LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }

    @Override
    public Date getCurrentDate() {
        return new Date();
    }

    @Override
    public Instant getCurrentInstant() {
        return Instant.now();
    }
}
