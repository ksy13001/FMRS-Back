package com.ksy.fmrs.util.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface TimeProvider {

    LocalDateTime getCurrentLocalDateTime();
    LocalDate getCurrentLocalDate();
    Date getCurrentDate();
    Instant getCurrentInstant();
}
