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

    private static final int MIN_PREFIX_LENGTH = 3;

    // 영어권 애칭 -> 정식 이름 (normalizeName 이후 기준: 대문자)
    private static final Map<String, String> NICKNAME_TO_FORMAL = Map.ofEntries(
            Map.entry("EDDIE", "EDWARD"),
            Map.entry("TED", "EDWARD"),
            Map.entry("ANDY", "ANDREW"),
            Map.entry("TOM", "THOMAS"),
            Map.entry("TOMMY", "THOMAS"),
            Map.entry("JOE", "JOSEPH"),
            Map.entry("JOEY", "JOSEPH"),
            Map.entry("JIMMY", "JAMES"),
            Map.entry("JIM", "JAMES"),
            Map.entry("JAMIE", "JAMES"),
            Map.entry("BOBBY", "ROBERT"),
            Map.entry("ROBBIE", "ROBERT"),
            Map.entry("BOB", "ROBERT"),
            Map.entry("BILLY", "WILLIAM"),
            Map.entry("BILL", "WILLIAM"),
            Map.entry("WILL", "WILLIAM"),
            Map.entry("DANNY", "DANIEL"),
            Map.entry("DAN", "DANIEL"),
            Map.entry("MICKY", "MICHAEL"),
            Map.entry("MICKEY", "MICHAEL"),
            Map.entry("MIKE", "MICHAEL"),
            Map.entry("TONY", "ANTHONY"),
            Map.entry("HARRY", "HENRY"),
            Map.entry("CHARLIE", "CHARLES"),
            Map.entry("FREDDIE", "FREDERICK"),
            Map.entry("FRED", "FREDERICK"),
            Map.entry("ALFIE", "ALFRED"),
            Map.entry("DELE", "BAMIDELE"),
            Map.entry("KENNY", "KENNETH"),
            Map.entry("KEN", "KENNETH"),
            Map.entry("RICKY", "RICHARD"),
            Map.entry("NICKY", "NICHOLAS"),
            Map.entry("NICK", "NICHOLAS"),
            Map.entry("OLLIE", "OLIVER"),
            Map.entry("STEVE", "STEPHEN"),
            Map.entry("STEVIE", "STEPHEN"),
            Map.entry("DAVE", "DAVID"),
            Map.entry("PHIL", "PHILIP"),
            Map.entry("MATTY", "MATTHEW"),
            Map.entry("MATT", "MATTHEW"),
            Map.entry("PADDY", "PATRICK"),
            Map.entry("PAT", "PATRICK"),
            Map.entry("GREG", "GREGORY"),
            Map.entry("SAMMY", "SAMUEL"),
            Map.entry("SAM", "SAMUEL"),
            Map.entry("BENNY", "BENJAMIN"),
            Map.entry("BEN", "BENJAMIN"),
            Map.entry("ALEX", "ALEXANDER"),
            Map.entry("CHRIS", "CHRISTOPHER"),
            Map.entry("MAX", "MAXIMILIAN"),
            Map.entry("GABE", "GABRIEL"),
            Map.entry("ZACK", "ZACHARY"),
            Map.entry("ZAK", "ZACHARY"),
            Map.entry("JACK", "JOHN"),
            Map.entry("JOHNNY", "JONATHAN")
    );

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

        // auto 분기도 동점(top1 == top2)이면 임의 매칭이 되므로 DUPLICATE로 보낸다 (TOKEN_SUBSET은 1.0 동점이 흔함)
        if(top1Score >= fuzzyMappingProperties.autoMatchThreshold() && top1Score > top2Score
                || top1Score >= fuzzyMappingProperties.relaxedMatchThreshold() && top1Score - top2Score >= fuzzyMappingProperties.minMargin()){
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
            case STANDARD_FULL_NAME, TOKEN_SUBSET -> buildNormalizedFullName(player.getFirstName(), player.getLastName());
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
            case FM_NAME_FIRST_FIRST_TOKEN, FM_NAME_FIRST_LAST_TOKEN, TOKEN_SUBSET ->
                    StringUtils.normalizeName(candidate.getName());
        };
    }

    private List<ScoredCandidate> scoreBestCandidateByFmUid(FuzzyStrategy strategy, String playerName, List<FmPlayer> candidates) {
        Map<Integer, ScoredCandidate> bestCandidateByFmUid = new HashMap<>();

        for (FmPlayer candidate : candidates) {
            String fmName = buildFmCompareName(strategy, candidate);

            if (isBlank(fmName)) {
                continue;
            }

            double score = scoreName(strategy, playerName, fmName);
            ScoredCandidate current = bestCandidateByFmUid.get(candidate.getFmUid());

            if (current == null || score > current.score()) {
                bestCandidateByFmUid.put(candidate.getFmUid(), new ScoredCandidate(candidate, score));
            }
        }

        return new ArrayList<>(bestCandidateByFmUid.values());
    }

    private double scoreName(FuzzyStrategy strategy, String playerName, String fmName) {
        if (strategy == FuzzyStrategy.TOKEN_SUBSET && isTokenSubset(fmName, playerName)) {
            return 1.0;
        }
        return similarity.apply(playerName, fmName);
    }

    /**
     * FM known-as 이름의 모든 토큰이 player 전체 이름 토큰으로 커버되면 true.
     * 커버 조건: 완전 일치 / player 토큰의 접두사(길이>=3) / 영어 애칭 사전.
     * 토큰 순서는 무시하므로 아시아권 이름 어순 뒤집힘도 매칭된다.
     */
    private boolean isTokenSubset(String fmName, String playerName) {
        String[] fmTokens = fmName.split("\\s+");
        String[] playerTokens = playerName.split("\\s+");

        for (String fmToken : fmTokens) {
            if (!isTokenCovered(fmToken, playerTokens)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTokenCovered(String fmToken, String[] playerTokens) {
        for (String playerToken : playerTokens) {
            if (playerToken.equals(fmToken)) {
                return true;
            }
            if (fmToken.length() >= MIN_PREFIX_LENGTH && playerToken.startsWith(fmToken)) {
                return true;
            }
            String formalName = NICKNAME_TO_FORMAL.get(fmToken);
            if (formalName != null && formalName.equals(playerToken)) {
                return true;
            }
        }
        return false;
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
