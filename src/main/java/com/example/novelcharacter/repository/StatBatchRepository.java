package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Stat.entity.Stat;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class StatBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void statBatchInsert(List<Stat> stats){
        String sql = "insert into Stat values (statName)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
           @Override
           public void setValues(PreparedStatement ps, int i) throws SQLException {
                Stat stat = stats.get(i);
                ps.setString(1, stat.getStatName());
           }

           @Override
           public int getBatchSize() {
               return stats.size();
           }
        });
    }
}
