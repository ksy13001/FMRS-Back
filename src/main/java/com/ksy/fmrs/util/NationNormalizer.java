package com.ksy.fmrs.util;

import com.ksy.fmrs.domain.Nation;

import java.util.HashMap;
import java.util.Map;

public class NationNormalizer{
    private static final Map<String, String> NATIONS = Map.of(
            "TURKEY", "TÜRKIYE",
            "CZECH REPUBLIC", "CZECHIA",
            "HONG KONG (CHINA PR)", "HONG KONG, CHINA",
            "KYRGYZSTAN", "KYRGYZ REPUBLIC",
            "ST. VINCENT / GRENADINES", "ST. VINCENT AND THE GRENADINES"
    );

    public static String normalize(String raw) {
        return NATIONS.getOrDefault(raw.toUpperCase(), raw);
    }
}
