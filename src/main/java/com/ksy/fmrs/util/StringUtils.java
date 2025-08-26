package com.ksy.fmrs.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
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

    public static Integer extractNumber(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String numeric = value.replaceAll("\\D+", "");
        if (numeric.isEmpty()) {
            return null;
        }
        return Integer.parseInt(numeric);
    }

    public static Optional<String> extractTokenFromBearer(String header) {
        if (header == null){
            log.info("Authorization header not found");
            return Optional.empty();
        }

        if(!header.startsWith("Bearer ") || header.length() < 8){
            log.info("Authorization header is invalid");
            return Optional.empty();
        }

        return Optional.of(header.substring(7));
    }
}
