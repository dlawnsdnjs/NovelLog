package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Stat.entity.Stat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatMapper {
    public void insertStat(Stat stat);
    public void insertStatList(List<Stat> statList);
    public List<Stat> selectStatList(List<String> statName);
    public Stat selectStat(long statCode);
    public Stat selectStat(String statName);
    public void updateStat(Stat stat);
    public int deleteStat(long statCode);
}
