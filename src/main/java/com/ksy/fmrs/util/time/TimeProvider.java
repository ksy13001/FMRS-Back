package com.ksy.fmrs.util.time;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime getCurrentTime();
}
