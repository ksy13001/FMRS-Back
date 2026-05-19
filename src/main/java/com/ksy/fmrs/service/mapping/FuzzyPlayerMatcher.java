package com.ksy.fmrs.service.mapping;

import com.ksy.fmrs.domain.enums.FuzzyStrategy;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.FuzzyMappingProperties;
import com.ksy.fmrs.dto.FuzzyMappingResult;
import com.ksy.fmrs.dto.ScoredCandidate;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FuzzyPlayerMatcher {

    private final FuzzyMappingProperties fuzzyMappingProperties;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    public FuzzyMappingResult match(FuzzyStrategy strategy, boolean dryRun, Player player, List<FmPlayer> candidates){
        return match(strategy, player, candidates);
    }

    public FuzzyMappingResult match(FuzzyStrategy strategy, Player player, List<FmPlayer> candidates){
        String playerName = buildPlayerCompareName(strategy, player);

        if (isBlank(playerName)) {
            return FuzzyMappingResult.noMatch(player.getId());
        }

        List<ScoredCandidate> scored = scoreBestCandidateByFmUid(strategy, playerName, candidates).stream()
                .sorted(Comparator.comparingDouble(ScoredCandidate::score).reversed())
                .toList();

        if(scored.isEmpty()){
            return FuzzyMappingResult.noMatch(player.getId());
        }

        ScoredCandidate top1 = scored.get(0);
        double top1Score = top1.score();
        double top2Score = scored.size() < 2 ? 0.0 : scored.get(1).score();
        int candidateCount = scored.size();
        Integer top1FmUid = top1.fmplayer().getFmUid();

        if(top1Score >= fuzzyMappingProperties.autoMatchThreshold() || top1Score >= fuzzyMappingProperties.relaxedMatchThreshold() && top1Score - top2Score >= fuzzyMappingProperties.minMargin()){
            return FuzzyMappingResult.matched(player.getId(), top1FmUid, candidateCount, top1Score, top2Score);
        } else if(top1Score >= fuzzyMappingProperties.relaxedMatchThreshold()){
            return FuzzyMappingResult.duplicate(player.getId(), candidateCount, top1Score, top2Score, top1FmUid);
        } else{
            return FuzzyMappingResult.noMatch(player.getId(), candidateCount, top1Score, top2Score, top1FmUid);
        }
    }

    private String buildNormalizedFullName(String firstName, String lastName) {
        String fullName = (nullToEmpty(firstName) + " " + nullToEmpty(lastName)).trim();
        return StringUtils.normalizeName(fullName);
    }

    private String buildPlayerCompareName(FuzzyStrategy strategy, Player player) {
        return switch (strategy) {
            case STANDARD_FULL_NAME -> buildNormalizedFullName(player.getFirstName(), player.getLastName());
            case FM_NAME_FIRST_FIRST_TOKEN -> buildNormalizedFullName(
                    firstToken(player.getFirstName()),
                    firstToken(player.getLastName())
            );
            case FM_NAME_FIRST_LAST_TOKEN -> buildNormalizedFullName(
                    firstToken(player.getFirstName()),
                    lastToken(player.getLastName())
            );
        };
    }

    private String buildFmCompareName(FuzzyStrategy strategy, FmPlayer candidate) {
        return switch (strategy) {
            case STANDARD_FULL_NAME -> buildNormalizedFullName(candidate.getFirstName(), candidate.getLastName());
            case FM_NAME_FIRST_FIRST_TOKEN, FM_NAME_FIRST_LAST_TOKEN -> StringUtils.normalizeName(candidate.getName());
        };
    }

    private List<ScoredCandidate> scoreBestCandidateByFmUid(FuzzyStrategy strategy, String playerName, List<FmPlayer> candidates) {
        Map<Integer, ScoredCandidate> bestCandidateByFmUid = new HashMap<>();

        for (FmPlayer candidate : candidates) {
            String fmName = buildFmCompareName(strategy, candidate);

            if (isBlank(fmName)) {
                continue;
            }

            double score = similarity.apply(playerName, fmName);
            ScoredCandidate current = bestCandidateByFmUid.get(candidate.getFmUid());

            if (current == null || score > current.score()) {
                bestCandidateByFmUid.put(candidate.getFmUid(), new ScoredCandidate(candidate, score));
            }
        }

        return new ArrayList<>(bestCandidateByFmUid.values());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String firstToken(String value) {
        if (isBlank(value)) {
            return "";
        }
        return value.trim().split("\\s+")[0];
    }

    private String lastToken(String value) {
        if (isBlank(value)) {
            return "";
        }
        String[] tokens = value.trim().split("\\s+");
        return tokens[tokens.length - 1];
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
