package com.ksy.fmrs.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "mapping.fuzzy")
public record FuzzyMappingProperties(
        double autoMatchThreshold,
        double relaxedMatchThreshold,
        double minMargin
) {
    public FuzzyMappingProperties {
        if (autoMatchThreshold <= 0 || autoMatchThreshold > 1) {
            throw new IllegalArgumentException("mapping.fuzzy.auto-match-threshold must be between 0 and 1");
        }
        if (relaxedMatchThreshold <= 0 || relaxedMatchThreshold > 1) {
            throw new IllegalArgumentException("mapping.fuzzy.relaxed-match-threshold must be between 0 and 1");
        }
        if (minMargin <= 0 || minMargin > 1) {
            throw new IllegalArgumentException("mapping.fuzzy.min-margin must be between 0 and 1");
        }
        if (relaxedMatchThreshold > autoMatchThreshold) {
            throw new IllegalArgumentException("mapping.fuzzy.relaxed-match-threshold must be less than or equal to auto-match-threshold");
        }
    }
}
