package com.ksy.fmrs.util.time;

import java.time.LocalDate;
import java.time.Period;

public class                                                                                                                                 TimeUtils {
    private TimeUtils() {

    }

    public static int getAge(LocalDate birth) {
        if(birth == null){
            return 0;
        }
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
