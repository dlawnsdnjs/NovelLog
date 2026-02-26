package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatRequestDTO;
import com.example.novelcharacter.domain.Equipment.entity.EquipmentStat;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class EquipmentStatBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void equipmentStatBatchInsert(long equipmentNum ,final List<EquipmentStatRequestDTO> equipmentStatList) {
        String sql = "insert into EquipmentStat (equipmentNum, statCode, statType, value) values (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                EquipmentStatRequestDTO equipmentStat = equipmentStatList.get(i);
                ps.setLong(1, equipmentNum);
                ps.setLong(2, equipmentStat.getStat().getStatCode());
                ps.setInt(3, equipmentStat.getType());
                ps.setLong(4, equipmentStat.getStat().getValue());
            }

            @Override
            public int getBatchSize() {
                return equipmentStatList.size();
            }
        });
    }

}
