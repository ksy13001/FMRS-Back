package com.ksy.fmrs.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Test
    void StringToLocalDate(){
        String date = "1998-10-07";
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        System.out.println(localDate);
    }
}