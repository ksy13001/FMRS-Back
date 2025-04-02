package com.ksy.fmrs.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class JacksonConfig {

    @Bean
    public Module flexibleLocalDateModule() {
        SimpleModule module = new SimpleModule("FlexibleLocalDateModule");
        // LocalDate 역직렬화(파싱) - 월, 일이 한자리일 경우 처리
        module.addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer());
        return module;
    }
}
