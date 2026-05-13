package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.enums.MappingStatus;

public record FuzzyMappingResult(
        Long playerId,
        MappingStatus mappingStatus,
        Integer matchedFmUid,
        int candidateCount,
        Double top1Score,
        Double top2Score,
        Integer top1FmUid
) {
    public static FuzzyMappingResult matched(
            Long playerId,
            Integer fmUid,
            int candidateCount,
            double top1Score,
            double top2Score
    ){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.MATCHED,
                fmUid,
                candidateCount,
                top1Score,
                top2Score,
                fmUid
        );
    }

    public static FuzzyMappingResult duplicate(
            Long playerId,
            int candidateCount,
            double top1Score,
            double top2Score,
            Integer top1FmUid
    ){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.DUPLICATE,
                null,
                candidateCount,
                top1Score,
                top2Score,
                top1FmUid
        );
    }

    public static FuzzyMappingResult noMatch(Long playerId){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.NO_MATCH,
                null,
                0,
                null,
                null,
                null
        );
    }

    public static FuzzyMappingResult noMatch(
            Long playerId,
            int candidateCount,
            double top1Score,
            double top2Score,
            Integer top1FmUid
    ){
        return new FuzzyMappingResult(
                playerId,
                MappingStatus.NO_MATCH,
                null,
                candidateCount,
                top1Score,
                top2Score,
                top1FmUid
        );
    }
}
