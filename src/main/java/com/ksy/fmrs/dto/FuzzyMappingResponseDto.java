package com.ksy.fmrs.dto;

public record FuzzyMappingResponseDto(
        long processedPlayers,
        long candidateKeyCount,
        long candidateQueryCount,
        long candidateFmPlayerCount,
        long matchedCandidates,
        long duplicateCandidates,
        long noMatchCandidates,
        long linkedFmPlayerRows,
        long matchedPlayersUpdated,
        long duplicatePlayersUpdated,
        long refreshedPlayers
) {
}
