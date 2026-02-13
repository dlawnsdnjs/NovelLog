package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.RefreshDTO;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshMapper {
    void insertRefresh(RefreshDTO refreshDTO);

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}
