package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Equipment.entity.EquipmentStat;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatInfoDTO;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatInfoWithNumDTO;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentStatMapper {
    public EquipmentStat selectEquipmentStatByIds(long equipmentNum, long statCode, String statType);
    public List<EquipmentStatInfoWithNumDTO> selectEquipmentStatsByIds(List<Long> equipmentNum);
    public List<EquipmentStatInfoDTO> selectEquipmentStatsById(long equipmentNum);
    public void insertEquipmentStat(EquipmentStat equipmentStat);
    public void insertEquipmentStatList(long equipmentNum, List<EquipmentStatRequestDTO> stats);
    public void updateEquipmentStat(EquipmentStat equipmentStat);
    public void deleteEquipmentStat(EquipmentStat equipmentStat);
}
