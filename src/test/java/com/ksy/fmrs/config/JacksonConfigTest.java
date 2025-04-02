package com.ksy.fmrs.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestConfiguration
class JacksonConfigTest {

    @Bean
    public Module flexibleLocalDateModule() {
        SimpleModule module = new SimpleModule("FlexibleLocalDateModule");
        // LocalDate 역직렬화(파싱) - 월, 일이 한자리일 경우 처리
        module.addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer());
        return module;
    }
}