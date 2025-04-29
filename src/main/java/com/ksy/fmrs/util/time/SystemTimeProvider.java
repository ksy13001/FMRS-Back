package com.ksy.fmrs.util.time;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Primary
@Component
public class SystemTimeProvider implements TimeProvider {

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
