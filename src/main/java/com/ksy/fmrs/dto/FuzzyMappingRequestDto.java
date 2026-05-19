package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.enums.FuzzyStrategy;

public record FuzzyMappingRequestDto(FuzzyStrategy strategy, boolean dryRun){
}