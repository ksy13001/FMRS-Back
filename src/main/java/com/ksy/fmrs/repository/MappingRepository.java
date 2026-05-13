package com.ksy.fmrs.repository;

import com.ksy.fmrs.dto.FuzzyMappingResult;
import com.ksy.fmrs.util.SqlLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MappingRepository {

    private static final String SQL_DIR = "db/sql/";
    private static final int BATCH_SIZE = 500;
    private final JdbcTemplate jdbcTemplate;
    private final SqlLoader sqlLoader;

    public int markPlayersWithMissingMappingKeysAsFailed() {
        String query = sqlLoader.load(SQL_DIR + "mark_players_with_missing_mapping_keys_as_failed.sql");
        return jdbcTemplate.update(query);
    }

    public int markPlayersWithDuplicateFmPlayerCandidates() {
        String query = sqlLoader.load(SQL_DIR + "mark_players_with_duplicate_fmplayer_candidates.sql");
        return jdbcTemplate.update(query);
    }

    public int propagatePlayerIdByFmUid() {
        String query = sqlLoader.load(SQL_DIR + "propagate_player_id_by_fm_uid.sql");
        return jdbcTemplate.update(query);
    }

    public int assignPlayerIdToExactMatchedFmPlayers() {
        String query = sqlLoader.load(SQL_DIR + "link_exact_matched_fmplayers_to_players.sql");
        return jdbcTemplate.update(query);
    }

    public int markRemainingPlayersAsNoMatch() {
        String query = sqlLoader.load(SQL_DIR + "mark_remaining_players_as_no_match.sql");
        return jdbcTemplate.update(query);
    }

    public int markPlayersWithLinkedFmPlayersAsMatched() {
        String query = sqlLoader.load(SQL_DIR + "mark_players_with_linked_fmplayers_as_matched_and_EXACT_4KEY.sql");
        return jdbcTemplate.update(query);
    }

    public int refreshPlayersLastFmData(){
        String query = sqlLoader.load(SQL_DIR + "update_players_last_fm_data.sql");
        return jdbcTemplate.update(query);
    }

    public int[][] linkFuzzyMatchedFmPlayersToPlayers(List<FuzzyMappingResult> matchedResults) {
        String query = """
                UPDATE fmplayer target
                LEFT JOIN (
                    SELECT DISTINCT fm_uid
                    FROM fmplayer
                    WHERE player_id IS NOT NULL
                      AND player_id <> ?
                ) conflict ON conflict.fm_uid = target.fm_uid
                SET target.player_id = ?
                WHERE target.fm_uid = ?
                  AND target.player_id IS NULL
                  AND conflict.fm_uid IS NULL
                """;

        return jdbcTemplate.batchUpdate(
                query,
                matchedResults,
                BATCH_SIZE,
                (ps, result) -> {
                    ps.setLong(1, result.playerId());
                    ps.setLong(2, result.playerId());
                    ps.setInt(3, result.matchedFmUid());
                }
        );
    }

    public int[][] markPlayersAsFuzzyMatched(List<FuzzyMappingResult> matchedResults) {
        String query = """
                UPDATE player
                SET mapping_status = 'MATCHED',
                    mapping_method = 'FUZZY_JARO_WINKLER'
                WHERE id = ?
                  AND mapping_status = 'NO_MATCH'
                  AND EXISTS (
                      SELECT 1
                      FROM fmplayer
                      WHERE player_id = ?
                        AND fm_uid = ?
                  )
                """;

        return jdbcTemplate.batchUpdate(
                query,
                matchedResults,
                BATCH_SIZE,
                (ps, result) -> {
                    ps.setLong(1, result.playerId());
                    ps.setLong(2, result.playerId());
                    ps.setInt(3, result.matchedFmUid());
                }
        );
    }

    public int[][] markPlayersAsDuplicate(List<FuzzyMappingResult> duplicateResults) {
        String query = """
                UPDATE player
                SET mapping_status = 'DUPLICATE',
                    mapping_method = NULL
                WHERE id = ?
                  AND mapping_status = 'NO_MATCH'
                """;

        return jdbcTemplate.batchUpdate(
                query,
                duplicateResults,
                BATCH_SIZE,
                (ps, result) -> ps.setLong(1, result.playerId())
        );
    }
}
