package com.ksy.fmrs.util;

import java.util.Map;
import java.util.Locale;

import static java.util.Map.entry;

public class NationNormalizer{
    private static final Map<String, String> NATIONS = Map.ofEntries(
            entry("TURKEY", "TÜRKIYE"),
            entry("TÃ¼RKIYE", "TÜRKIYE"),          // UTF-8 인코딩 깨진 케이스
            entry("CZECH REPUBLIC", "CZECHIA"),
            entry("FYR MACEDONIA", "NORTH MACEDONIA"),
            entry("HONG KONG", "HONG KONG, CHINA"),
            entry("HONG KONG (CHINA PR)", "HONG KONG, CHINA"),
            entry("KYRGYZSTAN", "KYRGYZ REPUBLIC"),
            entry("ST. VINCENT / GRENADINES", "ST. VINCENT AND THE GRENADINES"),
            entry("SOUTH KOREA", "KOREA REPUBLIC"),
            entry("REPUBLIC KOR", "KOREA REPUBLIC"),
            entry("REPBULIC KOR", "KOREA REPUBLIC"),
            entry("THE NETHERLANDS", "NETHERLANDS"),
            entry("COTE D'IVOIRE", "CÔTE D'IVOIRE"),
            entry("IVORY COAST", "CÔTE D'IVOIRE"),
            entry("BOSNIA-HERZEGOVINA", "BOSNIA AND HERZEGOVINA"),
            entry("DEMOCRATIC REPUBLIC OF CONGO", "CONGO DR"),
            entry("CAPE VERDE", "CAPE VERDE ISLANDS"),
            entry("CURACAO", "CURAÇAO"),
            entry("SAO TOME E PRINCIPE", "SÃO TOMÉ E PRÍNCIPE"),
            entry("UNITED STATES", "USA"),
            entry("SWAZILAND", "ESWATINI"),
            entry("THE GAMBIA", "GAMBIA"),
            entry("CHINESE TAIPEI (CHINA PR)", "CHINESE TAIPEI"),
            entry("BRUNEI", "BRUNEI DARUSSALAM"),
            entry("MACAU", "MACAO")
    );

    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.toUpperCase(Locale.ROOT);
        return NATIONS.getOrDefault(normalized, normalized);
    }
}
