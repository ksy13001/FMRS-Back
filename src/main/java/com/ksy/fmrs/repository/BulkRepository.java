package com.ksy.fmrs.repository;


import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.util.SqlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BulkRepository {
    private final JdbcTemplate jdbcTemplate;

    public void bulkUpsertPlayers(List<Player> players) {
        String sql = "INSERT INTO player " +
                "(player_api_id, name, first_name, last_name, nation_name, nation_logo_url, birth, height, weight, image_url, mapping_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) AS new " +
                "ON DUPLICATE KEY UPDATE " +
                "name = new.name, " +
                "first_name = new.first_name, " +
                "last_name = new.last_name, " +
                "nation_name = new.nation_name, " +
                "nation_logo_url = new.nation_logo_url, " +
                "birth = COALESCE(new.birth, player.birth) , " +
                "height = COALESCE(new.height, player.height), " +
                "weight = COALESCE(new.weight, player.weight), " +
                "image_url = new.image_url, " +
                "mapping_status = new.mapping_status";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Player player = players.get(i);
                ps.setInt(1, player.getPlayerApiId());
                ps.setString(2, player.getName());
                ps.setString(3, player.getFirstName());
                ps.setString(4, player.getLastName());
                ps.setString(5, player.getNationName());
                ps.setString(6, player.getNationLogoUrl());
                ps.setObject(7, player.getBirth(), Types.DATE);
                ps.setObject(8, player.getHeight(), Types.INTEGER);
                ps.setObject(9, player.getWeight(), Types.INTEGER);
                ps.setString(10, player.getImageUrl());
                ps.setString(11, player.getMappingStatus().getValue());
            }

            @Override
            public int getBatchSize() {
                return players.size();
            }
        });
    }

    public void bulkUpsertTeams(List<Team> teams, Long leagueId) {
        String sql = """
                INSERT INTO team
                (name, team_api_id, logo_url, league_id)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                name=VALUES(name),
                team_api_id=VALUES(team_api_id),
                logo_url=VALUES(logo_url),
                league_id=VALUES(league_id)
                """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Team team = teams.get(i);
                ps.setString(1, team.getName());
                ps.setInt(2, team.getTeamApiId());
                ps.setString(3, team.getLogoUrl());
                ps.setLong(4, leagueId);
            }

            @Override
            public int getBatchSize() {
                return teams.size();
            }
        });
    }

    public int mappingPlayerAndFmPlayer() {
        String sql = """
                UPDATE player p
                JOIN fmplayer f
                  ON p.first_name   = f.first_name
                 AND p.last_name    = f.last_name
                 AND p.birth        = f.birth
                 AND p.nation_name  = f.nation_name
                SET p.fmplayer_id    = f.id,
                    p.mapping_status = 'MATCHED',
                    p.is_gk = IF(f.goalkeeper=20, TRUE, FALSE)
                WHERE p.mapping_status = 'UNMAPPED'
                """;
        return jdbcTemplate.update(sql);
    }


    public int updatePlayersAsFailedByDuplicatedFmPlayer() {
        String sql = """
                UPDATE player p
                JOIN (SELECT f.first_name, f.last_name, f.birth, f.nation_name, COUNT(*)
                FROM fmplayer f
                GROUP BY 1,2,3,4 HAVING COUNT(*) > 1
                ) AS dup
                	on p.first_name = dup.first_name
                	AND p.last_name = dup.last_name
                	AND p.birth = dup.birth
                	AND p.nation_name = dup.nation_name
                SET p.mapping_status="FAILED"
                WHERE p.mapping_status = 'UNMAPPED'
                """;
        return jdbcTemplate.update(sql);
    }

    public void bulkUpdatePlayersFmData(List<Player> players, List<FmPlayer> fmPlayers) {
        String sql = "UPDATE player p " +
//                "JOIN fmplayer f " +
//                "ON f.first_name = p.first_name " +
//                "AND f.last_name = p.last_name " +
//                "AND f.birth = p.birth " +
//                "AND f.nation_name = p.nation_name " +
                "SET " +
                "p.fmplayer_id = ?, p.mapping_status= 'MATCHED' " +
                "WHERE p.id=? AND p.mapping_status= 'UNMAPPED' ";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Player player = players.get(i);
                FmPlayer fmPlayer = fmPlayers.get(i);
                ps.setLong(1, fmPlayer.getId());
                ps.setLong(2, player.getId());
            }

            @Override
            public int getBatchSize() {
                return players.size();
            }
        });

    }

    public void bulkInsertPlayerRaws(List<String> jsons) {
        List<String> columns = Arrays.asList("json_raw", "created_at", "processed");
        String sql = SqlUtils.buildInsertSql("player_raw", columns);

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String json = jsons.get(i);
                ps.setString(1, json);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(3, false);
            }

            @Override
            public int getBatchSize() {
                return jsons.size();
            }
        });
    }

    public void  updatePlayersTeam(List<Integer> playerApiIds, Long teamId) {
        String sql = "UPDATE player " +
                "SET team_id = ? " +
                "WHERE player_api_id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, teamId);
                ps.setLong(2, playerApiIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return playerApiIds.size();
            }
        });
    }
}