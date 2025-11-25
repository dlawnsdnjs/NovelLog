package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Equipment.EquipmentStatDTO;
import com.example.novelcharacter.dto.Equipment.EquipmentStatInfoDTO;
import com.example.novelcharacter.dto.Equipment.EquipmentStatInfoWithNumDTO;
import com.example.novelcharacter.dto.Equipment.EquipmentStatRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentStatMapper {
    public EquipmentStatDTO selectEquipmentStatByIds(long equipmentNum, long statCode, String statType);
    public List<EquipmentStatInfoWithNumDTO> selectEquipmentStatsByIds(List<Long> equipmentNum);
    public List<EquipmentStatInfoDTO> selectEquipmentStatsById(long equipmentNum);
    public void insertEquipmentStat(EquipmentStatDTO equipmentStatDTO);
    public void insertEquipmentStatList(long equipmentNum, List<EquipmentStatRequestDTO> stats);
    public void updateEquipmentStat(EquipmentStatDTO equipmentStatDTO);
    public void deleteEquipmentStat(EquipmentStatDTO equipmentStatDTO);
}
