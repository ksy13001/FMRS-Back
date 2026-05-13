package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.enums.MappingStatus;

public record FuzzyMappingResult(
        Long playerId,
        MappingStatus mappingStatus,
        Integer matchedFmUid
) {
    public static FuzzyMappingResult matched(Long playerId, Integer fmUid){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.MATCHED,
                fmUid
        );
    }

    public static FuzzyMappingResult duplicate(Long playerId){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.DUPLICATE,
                null
        );
    }

    public static FuzzyMappingResult noMatch(Long playerId){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.NO_MATCH,
                null
        );
    }
}
