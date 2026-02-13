package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentDataDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentMapper {
    public Equipment selectEquipmentById(long equipmentNum);
    public List<Equipment> selectEquipmentsByIds(List<Long> equipmentIds);
    public List<Equipment> selectEquipmentsById(long novelNum);
    public List<Equipment> selectEquipmentsPageById(long novelNum, int offset);
    public Equipment selectEquipmentByName(String equipmentName, long novelNum);
    public EquipmentDataDTO selectEquipmentDataList(List<Long> equipmentNums);
    public int checkEquipmentOwner(long uuid, long equipmentNum);
    public void insertEquipment(Equipment equipment);
    public void updateEquipment(Equipment equipment);
    public void deleteEquipment(long equipmentNum);
}
