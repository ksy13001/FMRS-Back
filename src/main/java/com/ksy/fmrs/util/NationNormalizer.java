package com.ksy.fmrs.util;

import java.util.Map;

public class NationNormalizer{
    private static final Map<String, String> NATIONS = Map.of(
            "TURKEY", "TÜRKIYE",
            "TÃ¼RKIYE", "TÜRKIYE",          // UTF-8 인코딩 깨진 케이스
            "CZECH REPUBLIC", "CZECHIA",
            "FYR MACEDONIA", "NORTH MACEDONIA",
            "HONG KONG (CHINA PR)", "HONG KONG, CHINA",
            "KYRGYZSTAN", "KYRGYZ REPUBLIC",
            "ST. VINCENT / GRENADINES", "ST. VINCENT AND THE GRENADINES",
            "SOUTH KOREA", "KOREA REPUBLIC",
            "REPUBLIC KOR", "KOREA REPUBLIC",
            "REPBULIC KOR", "KOREA REPUBLIC"
    );

    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        return NATIONS.getOrDefault(raw.toUpperCase(), raw.toUpperCase());
    }
}
