package com.ksy.fmrs.repository;

import com.ksy.fmrs.util.SqlLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MappingRepository {

    private static final String SQL_DIR = "db/sql/";
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
}
