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
    public int propagatePlayerIdByFmUid(){
        return mappingRepository.linkExistingPlayerIdByFmUid();
    }

    @Transactional
    public int markDuplicates(){
        return mappingRepository.updateDuplicate();
    }

    @Transactional
    public int matchExact(){
        int linkedRows = mappingRepository.linkFmPlayersByExactMatch();
        int matchedPlayers = mappingRepository.markMatchedByLinkedFmPlayer();
        mappingRepository.markRemainingAsNoMatch();
        return linkedRows + matchedPlayers;
    }

    public int matchNoMatch(){
        return 0;
    }

    public int matchFuzzy(){
        return 0;
    }

    public int refreshLatestFmData(){
        return 0;
    }
}
