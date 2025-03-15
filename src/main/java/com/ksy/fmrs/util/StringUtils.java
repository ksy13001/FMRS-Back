package com.ksy.fmrs.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringUtils {

    private StringUtils(){}

    public static String getLastName(String name){
        String[] split = name.split(" ");
        return split[split.length-1];
    }

    public static String getFirstName(String name){
        String[] split = name.split(" ");
        return split[0];
    }

    public static String getPlayerNameFromFileName(String fileName) {
        if(fileName == null) {
            throw new IllegalArgumentException("fileName is null");
        }
        // 확장자(.json) 제거
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            fileName = fileName.substring(0, dotIndex);
        }
        // 첫 번째 하이픈 위치 찾기
        int hyphenIndex = fileName.indexOf('-');
        if (hyphenIndex == -1 || hyphenIndex == fileName.length() - 1) {
            // 하이픈이 없거나 하이픈이 마지막이면 전체 문자열 반환
            return fileName.trim();
        }
        // 첫 번째 하이픈 이후의 모든 문자열을 이름으로 사용 (이름에 하이픈이 포함될 수 있음)
        return fileName.substring(hyphenIndex + 1).trim();
    }

    public static String truncateToTwoDecimalsRanging(String r) {
        if (r == null || r.isEmpty()) {
            return "0";
        }
        double rating = Double.parseDouble(r);
        return String.format("%.2f", rating);
    }

    public static LocalDate parseStringToLocalDate(String date) {
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getFirstChar(String name){
        if (name == null) {
            return null;
        }
        return name.substring(0, 1);
    }
}
