package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.BirthNationKey;
import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import com.ksy.fmrs.dto.FuzzyMappingResult;
import com.ksy.fmrs.repository.MappingRepository;
import com.ksy.fmrs.repository.Player.FmPlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.service.mapping.FuzzyPlayerMatcher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MappingService {
    private static final int FUZZY_CANDIDATE_KEY_BATCH_SIZE = 500;
    private static final int FUZZY_SCORING_LOG_INTERVAL = 5_000;
    private static final int FUZZY_DIAGNOSTIC_SAMPLE_SIZE = 10;

    private final MappingRepository mappingRepository;
    private final PlayerRepository playerRepository;
    private final FmPlayerRepository  fmPlayerRepository;
    private final FuzzyPlayerMatcher fuzzyPlayerMatcher;

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

    @Transactional
    public FuzzyMappingResponseDto matchFuzzy(){
        return matchFuzzy("direct");
    }

    @Transactional
    public FuzzyMappingResponseDto matchFuzzy(String jobId){
        log.info("[mapping-job:{}] fuzzy mapping loading NO_MATCH players", jobId);
        List<Player> noMatchPlayers = playerRepository.findPlayersByMappingStatus(MappingStatus.NO_MATCH);
        log.info("[mapping-job:{}] fuzzy mapping loaded NO_MATCH players: {}", jobId, noMatchPlayers.size());

        List<BirthNationKey> candidateKeys = noMatchPlayers.stream()
                .map(BirthNationKey::from)
                .filter(BirthNationKey::isComplete)
                .distinct()
                .toList();
        log.info("[mapping-job:{}] fuzzy mapping candidate keys: {}, query batches: {}",
                jobId, candidateKeys.size(), getCandidateQueryCount(candidateKeys));

        List<FmPlayer> candidateFmPlayers = findUnlinkedFmPlayersByBirthNationKeys(jobId, candidateKeys);
        log.info("[mapping-job:{}] fuzzy mapping loaded candidate fmplayers: {}", jobId, candidateFmPlayers.size());

        Map<BirthNationKey, List<FmPlayer>> candidatesByKey = candidateFmPlayers.stream()
                .collect(Collectors.groupingBy(BirthNationKey::from));
        log.info("[mapping-job:{}] fuzzy mapping grouped candidate keys: {}", jobId, candidatesByKey.size());

        List<FuzzyMappingResult> results = new ArrayList<>(noMatchPlayers.size());

        for (int i = 0; i < noMatchPlayers.size(); i++) {
            Player player = noMatchPlayers.get(i);
            List<FmPlayer> fmPlayers = candidatesByKey.getOrDefault(BirthNationKey.from(player), List.of());
            results.add(fuzzyPlayerMatcher.match(player, fmPlayers));

            int processed = i + 1;
            if (processed % FUZZY_SCORING_LOG_INTERVAL == 0 || processed == noMatchPlayers.size()) {
                log.info("[mapping-job:{}] fuzzy mapping scored players: {}/{}", jobId, processed, noMatchPlayers.size());
            }
        }

        List<FuzzyMappingResult> matchedResults = results.stream()
                .filter(result -> result.mappingStatus()
                        .equals(MappingStatus.MATCHED)).toList();

        List<FuzzyMappingResult> duplicatedResults = results.stream()
                .filter(result -> result.mappingStatus()
                        .equals(MappingStatus.DUPLICATE)).toList();

        long noMatchCandidates = results.size() - matchedResults.size() - duplicatedResults.size();
        logFuzzyDiagnostics(jobId, results);
        log.info("[mapping-job:{}] fuzzy mapping scoring result: matched={}, duplicate={}, noMatch={}",
                jobId, matchedResults.size(), duplicatedResults.size(), noMatchCandidates);

        long linkedFmPlayerRows = sumUpdatedRows(mappingRepository.linkFuzzyMatchedFmPlayersToPlayers(matchedResults));
        log.info("[mapping-job:{}] fuzzy mapping linked fmplayer rows: {}", jobId, linkedFmPlayerRows);

        long matchedPlayersUpdated = sumUpdatedRows(mappingRepository.markPlayersAsFuzzyMatched(matchedResults));
        log.info("[mapping-job:{}] fuzzy mapping updated matched players: {}", jobId, matchedPlayersUpdated);

        long duplicatePlayersUpdated = sumUpdatedRows(mappingRepository.markPlayersAsDuplicate(duplicatedResults));
        log.info("[mapping-job:{}] fuzzy mapping updated duplicate players: {}", jobId, duplicatePlayersUpdated);

        long refreshedPlayers = matchedPlayersUpdated > 0
                ? mappingRepository.refreshPlayersLastFmData()
                : 0;
        log.info("[mapping-job:{}] fuzzy mapping refreshed players: {}", jobId, refreshedPlayers);

        return new FuzzyMappingResponseDto(
                results.size(),
                candidateKeys.size(),
                getCandidateQueryCount(candidateKeys),
                candidateFmPlayers.size(),
                matchedResults.size(),
                duplicatedResults.size(),
                noMatchCandidates,
                linkedFmPlayerRows,
                matchedPlayersUpdated,
                duplicatePlayersUpdated,
                refreshedPlayers
        );
    }

    @Transactional
    public int refreshLatestFmData(){
        return mappingRepository.refreshPlayersLastFmData();
    }

    private long sumUpdatedRows(int[][] batchResults) {
        long sum = 0;

        for (int[] batchResult : batchResults) {
            for (int updatedRows : batchResult) {
                if (updatedRows > 0) {
                    sum += updatedRows;
                }
            }
        }

        return sum;
    }

    private List<FmPlayer> findUnlinkedFmPlayersByBirthNationKeys(String jobId, List<BirthNationKey> keys) {
        List<FmPlayer> candidates = new ArrayList<>();

        for (int start = 0; start < keys.size(); start += FUZZY_CANDIDATE_KEY_BATCH_SIZE) {
            int end = Math.min(start + FUZZY_CANDIDATE_KEY_BATCH_SIZE, keys.size());
            long startedAt = System.currentTimeMillis();
            List<FmPlayer> batchCandidates = fmPlayerRepository.findUnlinkedFmPlayersByBirthNationKeys(keys.subList(start, end));
            candidates.addAll(batchCandidates);

            log.info(
                    "[mapping-job:{}] fuzzy mapping loaded candidate key batch: {}/{} batchCandidates={} totalCandidates={} elapsedMs={}",
                    jobId,
                    end,
                    keys.size(),
                    batchCandidates.size(),
                    candidates.size(),
                    System.currentTimeMillis() - startedAt
            );
        }

        return candidates;
    }

    private long getCandidateQueryCount(List<BirthNationKey> keys) {
        if (keys.isEmpty()) {
            return 0;
        }

        return (keys.size() + FUZZY_CANDIDATE_KEY_BATCH_SIZE - 1L) / FUZZY_CANDIDATE_KEY_BATCH_SIZE;
    }

    private void logFuzzyDiagnostics(String jobId, List<FuzzyMappingResult> results) {
        long emptyCandidateCount = results.stream()
                .filter(result -> result.candidateCount() == 0)
                .count();
        long hasCandidateCount = results.size() - emptyCandidateCount;

        long top1Gte90 = countTop1ScoreGte(results, 0.90);
        long top1Gte80 = countTop1ScoreGte(results, 0.80);
        long top1Gte70 = countTop1ScoreGte(results, 0.70);
        long top1Gte60 = countTop1ScoreGte(results, 0.60);

        long singleCandidate = results.stream()
                .filter(result -> result.candidateCount() == 1)
                .count();
        long twoToFiveCandidates = results.stream()
                .filter(result -> result.candidateCount() >= 2 && result.candidateCount() <= 5)
                .count();
        long moreThanFiveCandidates = results.stream()
                .filter(result -> result.candidateCount() > 5)
                .count();

        log.info(
                "[mapping-job:{}] fuzzy diagnostics candidate coverage: empty={}, hasCandidate={}",
                jobId,
                emptyCandidateCount,
                hasCandidateCount
        );
        log.info(
                "[mapping-job:{}] fuzzy diagnostics top1 score buckets: >=0.90={}, >=0.80={}, >=0.70={}, >=0.60={}",
                jobId,
                top1Gte90,
                top1Gte80,
                top1Gte70,
                top1Gte60
        );
        log.info(
                "[mapping-job:{}] fuzzy diagnostics candidate count buckets: one={}, twoToFive={}, moreThanFive={}",
                jobId,
                singleCandidate,
                twoToFiveCandidates,
                moreThanFiveCandidates
        );

        results.stream()
                .filter(result -> result.top1Score() != null)
                .sorted((left, right) -> Double.compare(right.top1Score(), left.top1Score()))
                .limit(FUZZY_DIAGNOSTIC_SAMPLE_SIZE)
                .forEach(result -> log.info(
                        "[mapping-job:{}] fuzzy diagnostics top sample: playerId={}, status={}, candidateCount={}, top1Score={}, top2Score={}, top1FmUid={}",
                        jobId,
                        result.playerId(),
                        result.mappingStatus(),
                        result.candidateCount(),
                        result.top1Score(),
                        result.top2Score(),
                        result.top1FmUid()
                ));
    }

    private long countTop1ScoreGte(List<FuzzyMappingResult> results, double threshold) {
        return results.stream()
                .filter(result -> result.top1Score() != null)
                .filter(result -> result.top1Score() >= threshold)
                .count();
    }
}
