package com.ksy.fmrs.util;

public class StringUtils {

    private StringUtils(){}

    public static String getLastName(String name){
        String[] split = name.split(" ");
        return split[split.length-1];
    }
}
