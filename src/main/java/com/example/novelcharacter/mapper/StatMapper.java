package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Stat.StatDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatMapper {
    public void insertStat(StatDTO statDTO);
    public void insertStatList(List<StatDTO> statDTOList);
    public List<StatDTO> selectStatList(List<String> statName);
    public StatDTO selectStat(long statCode);
    public StatDTO selectStat(String statName);
    public void updateStat(StatDTO statDTO);
    public int deleteStat(long statCode);
}
