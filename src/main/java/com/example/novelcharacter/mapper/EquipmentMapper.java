package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Equipment.EquipmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentMapper {
    public EquipmentDTO selectEquipmentById(long equipmentNum);
    public List<EquipmentDTO> selectEquipmentsByIds(List<Long> equipmentIds);
    public List<EquipmentDTO> selectEquipmentsById(long novelNum);
    public List<EquipmentDTO> selectEquipmentsPageById(long novelNum, int offset);
    public EquipmentDTO selectEquipmentByName(String equipmentName, long novelNum);
    public int checkEquipmentOwner(long uuid, long equipmentNum);
    public void insertEquipment(EquipmentDTO equipment);
    public void updateEquipment(EquipmentDTO equipment);
    public void deleteEquipment(long equipmentNum);
}
