package com.ksy.fmrs.service;

import com.ksy.fmrs.repository.MappingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MappingService {
    private final MappingRepository mappingRepository;

    @Transactional
    public int markPlayersWithMissingMappingKeysAsFailed() {
        return mappingRepository.markPlayersWithMissingMappingKeysAsFailed();
    }

    @Transactional
    public int propagatePlayerIdByFmUid(){
        return mappingRepository.propagatePlayerIdByFmUid();
    }

    @Transactional
    public int markDuplicates(){
        return mappingRepository.markPlayersWithDuplicateFmPlayerCandidates();
    }

    @Transactional
    public int matchExact(){
        int linkedRows = mappingRepository.assignPlayerIdToExactMatchedFmPlayers();
        int matchedPlayers = mappingRepository.markPlayersWithLinkedFmPlayersAsMatched();
        mappingRepository.markRemainingPlayersAsNoMatch();
        return linkedRows + matchedPlayers;
    }

    public int matchFuzzy(){
        return 0;
    }

    public int refreshLatestFmData(){
        return 0;
    }
}
