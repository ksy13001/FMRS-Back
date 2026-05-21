package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.FuzzyStrategy;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.BirthNationKey;
import com.ksy.fmrs.dto.FuzzyMappingProperties;
import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import com.ksy.fmrs.dto.FuzzyMappingResult;
import com.ksy.fmrs.repository.MappingRepository;
import com.ksy.fmrs.repository.Player.FmPlayerRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.service.mapping.FuzzyPlayerMatcher;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
    private static final int FUZZY_PLAYER_CHUNK_SIZE = 1_000;

    private final MappingRepository mappingRepository;
    private final PlayerRepository playerRepository;
    private final FmPlayerRepository  fmPlayerRepository;
    private final FuzzyPlayerMatcher fuzzyPlayerMatcher;
    private final FuzzyMappingProperties fuzzyMappingProperties;
    private final EntityManager entityManager;

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
    public int matchTokenName(){
        int linkedRows = mappingRepository.assignPlayerIdToTokenNameMatchedFmPlayers();
        int matchedPlayers = mappingRepository.markPlayersWithTokenNameLinkedFmPlayersAsMatched();

        if (matchedPlayers > 0) {
            mappingRepository.refreshPlayersLastFmData();
        }

        return linkedRows + matchedPlayers;
    }

    @Transactional
    public int matchFirstNameTokenAndFirstLastNameToken(){
        int linkedRows = mappingRepository.assignPlayerIdToFirstNameTokenAndFirstLastNameTokenMatchedFmPlayers();
        int matchedPlayers = mappingRepository.markPlayersWithFirstNameTokenAndFirstLastNameTokenLinkedFmPlayersAsMatched();

        if (matchedPlayers > 0) {
            mappingRepository.refreshPlayersLastFmData();
        }

        return linkedRows + matchedPlayers;
    }

    @Transactional
    public FuzzyMappingResponseDto matchFuzzy(String jobId, FuzzyStrategy strategy, boolean dryRun) {
        long lastPlayerId = 0L;
        long totalProcessedPlayers = 0;
        long totalCandidateKeyCount = 0;
        long totalCandidateQueryCount = 0;
        long totalCandidateFmPlayerCount = 0;
        long totalMatchedCandidates = 0;
        long totalDuplicateCandidates = 0;
        long totalNoMatchCandidates = 0;
        long totalLinkedFmPlayerRows = 0;
        long totalMatchedPlayersUpdated = 0;
        long totalDuplicatePlayersUpdated = 0;
        long totalRefreshedPlayers = 0;

        log.info(
                "[mapping-job:{}] fuzzy mapping started: strategy={}, dryRun={}, playerChunkSize={}, autoMatchThreshold={}, relaxedMatchThreshold={}, minMargin={}",
                jobId,
                strategy,
                dryRun,
                FUZZY_PLAYER_CHUNK_SIZE,
                fuzzyMappingProperties.autoMatchThreshold(),
                fuzzyMappingProperties.relaxedMatchThreshold(),
                fuzzyMappingProperties.minMargin()
        );

        while (true) {
            List<Player> noMatchPlayers = playerRepository.findPlayersByMappingStatusAfterId(
                    MappingStatus.NO_MATCH,
                    lastPlayerId,
                    PageRequest.of(0, FUZZY_PLAYER_CHUNK_SIZE)
            );

            if (noMatchPlayers.isEmpty()) {
                break;
            }

            FuzzyMappingResponseDto chunkResult = matchFuzzyChunk(jobId, strategy, dryRun, noMatchPlayers);

            totalProcessedPlayers += chunkResult.processedPlayers();
            totalCandidateKeyCount += chunkResult.candidateKeyCount();
            totalCandidateQueryCount += chunkResult.candidateQueryCount();
            totalCandidateFmPlayerCount += chunkResult.candidateFmPlayerCount();
            totalMatchedCandidates += chunkResult.matchedCandidates();
            totalDuplicateCandidates += chunkResult.duplicateCandidates();
            totalNoMatchCandidates += chunkResult.noMatchCandidates();
            totalLinkedFmPlayerRows += chunkResult.linkedFmPlayerRows();
            totalMatchedPlayersUpdated += chunkResult.matchedPlayersUpdated();
            totalDuplicatePlayersUpdated += chunkResult.duplicatePlayersUpdated();
            totalRefreshedPlayers += chunkResult.refreshedPlayers();

            lastPlayerId = noMatchPlayers.get(noMatchPlayers.size() - 1).getId();

            log.info(
                    "[mapping-job:{}] fuzzy mapping chunk completed: lastPlayerId={}, totalProcessed={}, totalMatched={}, totalDuplicate={}, totalNoMatch={}",
                    jobId,
                    lastPlayerId,
                    totalProcessedPlayers,
                    totalMatchedCandidates,
                    totalDuplicateCandidates,
                    totalNoMatchCandidates
            );
            entityManager.flush();
            entityManager.clear();
        }

        return new FuzzyMappingResponseDto(
                totalProcessedPlayers,
                totalCandidateKeyCount,
                totalCandidateQueryCount,
                totalCandidateFmPlayerCount,
                totalMatchedCandidates,
                totalDuplicateCandidates,
                totalNoMatchCandidates,
                totalLinkedFmPlayerRows,
                totalMatchedPlayersUpdated,
                totalDuplicatePlayersUpdated,
                totalRefreshedPlayers
        );
    }

    private FuzzyMappingResponseDto matchFuzzyChunk(String jobId, FuzzyStrategy strategy, boolean dryRun, List<Player> noMatchPlayers) {

        List<BirthNationKey> candidateKeys = noMatchPlayers.stream()
                .map(BirthNationKey::from)
                .filter(BirthNationKey::isComplete)
                .distinct()
                .toList();

        List<FmPlayer> candidateFmPlayers = findUnlinkedFmPlayersByBirthNationKeys(jobId, candidateKeys);

        Map<BirthNationKey, List<FmPlayer>> candidatesByKey = candidateFmPlayers.stream()
                .collect(Collectors.groupingBy(BirthNationKey::from));

        List<FuzzyMappingResult> results = new ArrayList<>(noMatchPlayers.size());

        for (Player player : noMatchPlayers) {
            List<FmPlayer> fmPlayers = candidatesByKey.getOrDefault(BirthNationKey.from(player), List.of());
            results.add(fuzzyPlayerMatcher.match(strategy, dryRun, player, fmPlayers));
        }

        List<FuzzyMappingResult> matchedResults = results.stream()
                .filter(result -> result.mappingStatus()
                        .equals(MappingStatus.MATCHED)).toList();

        List<FuzzyMappingResult> duplicatedResults = results.stream()
                .filter(result -> result.mappingStatus()
                        .equals(MappingStatus.DUPLICATE)).toList();

        long noMatchCandidates = results.size() - matchedResults.size() - duplicatedResults.size();
        logFuzzyDiagnostics(jobId, results);
        logFuzzyMatchedSamples(jobId, matchedResults);
        log.info("[mapping-job:{}] fuzzy mapping chunk scoring result: matched={}, duplicate={}, noMatch={}",
                jobId, matchedResults.size(), duplicatedResults.size(), noMatchCandidates);

        long linkedFmPlayerRows = dryRun
                ? 0
                : sumUpdatedRows(mappingRepository.linkFuzzyMatchedFmPlayersToPlayers(matchedResults));
        log.info("[mapping-job:{}] fuzzy mapping chunk linked fmplayer rows: {}", jobId, linkedFmPlayerRows);
        logFuzzyLinkSkippedWarning(jobId, dryRun, matchedResults, linkedFmPlayerRows);

        long matchedPlayersUpdated = dryRun
                ? 0
                : sumUpdatedRows(mappingRepository.markPlayersAsFuzzyMatched(matchedResults));
        log.info("[mapping-job:{}] fuzzy mapping chunk updated matched players: {}", jobId, matchedPlayersUpdated);

        long duplicatePlayersUpdated = dryRun
                ? 0
                : sumUpdatedRows(mappingRepository.markPlayersAsDuplicate(duplicatedResults));
        log.info("[mapping-job:{}] fuzzy mapping chunk updated duplicate players: {}", jobId, duplicatePlayersUpdated);

        long refreshedPlayers = !dryRun && matchedPlayersUpdated > 0
                ? mappingRepository.refreshPlayersLastFmData()
                : 0;
        log.info("[mapping-job:{}] fuzzy mapping chunk refreshed players: {}", jobId, refreshedPlayers);

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

    private void logFuzzyMatchedSamples(String jobId, List<FuzzyMappingResult> matchedResults) {
        if (matchedResults.isEmpty()) {
            return;
        }

        matchedResults.stream()
                .limit(FUZZY_DIAGNOSTIC_SAMPLE_SIZE)
                .forEach(result -> log.info(
                        "[mapping-job:{}] fuzzy matched candidate sample: playerId={}, fmUid={}, candidateCount={}, top1Score={}, top2Score={}",
                        jobId,
                        result.playerId(),
                        result.matchedFmUid(),
                        result.candidateCount(),
                        result.top1Score(),
                        result.top2Score()
                ));
    }

    private void logFuzzyLinkSkippedWarning(
            String jobId,
            boolean dryRun,
            List<FuzzyMappingResult> matchedResults,
            long linkedFmPlayerRows
    ) {
        if (dryRun || matchedResults.isEmpty() || linkedFmPlayerRows > 0) {
            return;
        }

        List<Integer> fmUids = matchedResults.stream()
                .map(FuzzyMappingResult::matchedFmUid)
                .distinct()
                .limit(50)
                .toList();

        log.warn(
                "[mapping-job:{}] fuzzy matched candidates were not linked: matchedCount={}, distinctFmUidSample={}",
                jobId,
                matchedResults.size(),
                fmUids
        );
    }
}
