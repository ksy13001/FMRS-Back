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

    //todo 현재 트랜잭션 내에 sql load 작업 들어간 상태 - 분리예정
    public int updateDuplicate() {
        String query = sqlLoader.load(SQL_DIR + "update_duplicate_player_with_fmplayer.sql");
        return jdbcTemplate.update(query);
    }

    public int linkExistingPlayerIdByFmUid() {
        String query = sqlLoader.load(SQL_DIR + "update_fmplayer_existing_playerId_by_fmUid.sql");
        return jdbcTemplate.update(query);
    }

    public int linkFmPlayersByExactMatch() {
        String query = sqlLoader.load(SQL_DIR + "link_fmplayer_by_exact_match.sql");
        return jdbcTemplate.update(query);
    }

    public int markRemainingAsNoMatch() {
        String query = sqlLoader.load(SQL_DIR + "mark_remaining_as_no_match.sql");
        return jdbcTemplate.update(query);
    }

    public int markMatchedByLinkedFmPlayer() {
        String query = sqlLoader.load(SQL_DIR + "update_matched_players.sql");
        return jdbcTemplate.update(query);
    }
}
