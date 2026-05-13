package com.ksy.fmrs.dto;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "mapping.fuzzy")
public record FuzzyMappingProperties(
        double autoMatchThreshold,
        double relaxedMatchThreshold,
        double minMargin
) {

}
