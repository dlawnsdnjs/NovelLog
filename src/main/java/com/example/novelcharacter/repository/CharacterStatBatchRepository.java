package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Episode.dto.EpisodeCharacterDTO;
import com.example.novelcharacter.domain.Episode.entity.CharacterStat;
import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class CharacterStatBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void characterStatBatchInsert(long episodeNum, long characterNum, List<StatRequestDTO> statRequestDTOS) {
        String sql = "insert into CharacterStat (episodeNum, characterNum, statCode, value) values (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StatRequestDTO statRequestDTO = statRequestDTOS.get(i);
                ps.setLong(1, episodeNum);
                ps.setLong(2, characterNum);
                ps.setLong(3, statRequestDTO.getStatCode());
                ps.setLong(4, statRequestDTO.getValue());
            }

            @Override
            public int getBatchSize() {
                return statRequestDTOS.size();
            }
        });
    }
}
