package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class TransferRepositoryJDBC{

    private final JdbcTemplate jdbcTemplate;


    public void saveAllOPKU(List<Transfer> transfers){
        String sql = """
                INSERT INTO transfer(
                    player_id, from_team_id, to_team_id, date, type, fee, currency, updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    id = id;
                """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Transfer transfer = transfers.get(i);
                ps.setLong(1, transfer.getPlayer().getId());

                if (transfer.getFromTeam() != null) {
                    ps.setLong(2, transfer.getFromTeam().getId());
                } else {
                    ps.setNull(2, Types.BIGINT);
                }

                if (transfer.getToTeam() != null) {
                    ps.setLong(3, transfer.getToTeam().getId());
                } else {
                    ps.setNull(3, Types.BIGINT);
                }

                if (transfer.getDate() != null) {
                    ps.setDate(4, Date.valueOf(transfer.getDate()));
                } else {
                    ps.setNull(4, Types.DATE);
                }

                if (transfer.getType() != null) {
                    ps.setString(5, transfer.getType().name());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }

                if (transfer.getFee() != null) {
                    ps.setDouble(6, transfer.getFee());
                } else {
                    ps.setNull(6, Types.DOUBLE);
                }

                if (transfer.getCurrency() != null) {
                    ps.setString(7, transfer.getCurrency());
                } else {
                    ps.setNull(7, Types.VARCHAR);
                }

                if (transfer.getUpdatedAt() != null) {
                    ps.setTimestamp(8, Timestamp.valueOf(transfer.getUpdatedAt()));
                } else {
                    ps.setNull(8, Types.TIMESTAMP);
                }
            }

            @Override
            public int getBatchSize() {
                return transfers.size();
            }
        });
    }
}
