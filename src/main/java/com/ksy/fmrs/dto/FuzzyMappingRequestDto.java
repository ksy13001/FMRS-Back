package com.ksy.fmrs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksy.fmrs.domain.enums.FuzzyStrategy;

public record FuzzyMappingRequestDto(
        @JsonProperty(value = "strategy", required = true) FuzzyStrategy strategy,
        @JsonProperty(value = "dryRun", required = true) Boolean dryRun
) {
    public FuzzyMappingRequestDto {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy is required");
        }
        if (dryRun == null) {
            throw new IllegalArgumentException("dryRun is required");
        }
    }
}
