package com.example.novelcharacter.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class CharacterEquipBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void characterEquipBatchInsert(long episodeNum, long characterNum, List<Long> equipmentNums) {
        String sql = "insert into CharacterEquip values (episodeNum, characterNum, equipmentNum)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
           @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                long equipmentNum = equipmentNums.get(i);
                ps.setLong(1, equipmentNum);
                ps.setLong(2, characterNum);
                ps.setLong(3, episodeNum);
           }

           @Override
            public int getBatchSize() {
               return equipmentNums.size();
           }
        });
    }
}
