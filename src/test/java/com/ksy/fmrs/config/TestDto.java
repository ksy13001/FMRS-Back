package com.ksy.fmrs.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;
class FlexibleLocalDateDeserializerTest {
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new FlexibleLocalDateDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    @DisplayName("월, 일 한자리 입력시 mm-dd 로 반환 테스트")
    void testDeserialize_ValidOneDigitMonthDay() throws Exception {
        // given
        String json = "{\"date\":\"1990-1-1\"}";

        // when
        TestDto result = objectMapper.readValue(json, TestDto.class);

        // then
        Assertions.assertThat(result.getDate()).isEqualTo("1990-01-01");
    }

    @Test
    @DisplayName("정상 localTime 입력 테스트")
    void testDeserialize_ValidTwoDigitMonthDay() throws Exception {
        // given
        String json = "{\"date\":\"1990-12-09\"}";

        // when
        TestDto result = objectMapper.readValue(json, TestDto.class);

        // then
        Assertions.assertThat(result.getDate()).isEqualTo("1990-12-09");
    }

    @Test
    @DisplayName("잘못된 포멧 테스트")
    void testDeserialize_InvalidFormat() {
        // given
        String json = "{\"date\":\"1990--1\"}";

        // when & then
        Assertions.assertThatThrownBy(() -> objectMapper.readValue(json, TestDto.class))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("날짜 형식이 잘못되었습니다");
    }

    @Test
    void testDeserialize_InvalidYear() {
        // given
        String json = "{\"date\":\"19-1-1\"}"; // 연도 4자리 아님

        // when & then
        Assertions.assertThatThrownBy(() -> objectMapper.readValue(json, TestDto.class))
                .isInstanceOf(InvalidFormatException.class)
                .hasMessageContaining("날짜 형식이 잘못되었습니다");
    }

    @Test
    @DisplayName("null")
    void nullTest() throws Exception{
        // given
        String json = "{\"date\":null}"; // 연도 4자리 아님

        TestDto result = objectMapper.readValue(json, TestDto.class);
        Assertions.assertThat(result.getDate()).isNull();
    }
}



public class TestDto{
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }
}