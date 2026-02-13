package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Equipment.dto.EquipmentDTO;
import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentDataDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentMapper {
    public EquipmentDTO selectEquipmentById(long equipmentNum);
    public List<EquipmentDTO> selectEquipmentsByIds(List<Long> equipmentNum);
    public List<EquipmentDTO> selectEquipmentsById(long novelNum);
    public List<EquipmentDTO> selectEquipmentsPageById(long novelNum, int offset);
    public EquipmentDTO selectEquipmentByName(String equipmentName, long novelNum);
    public List<EquipmentDataDTO> selectEquipmentDataList(List<Long> equipmentNum);
    public int checkEquipmentOwner(long uuid, long equipmentNum);
    public void insertEquipment(Equipment equipment);
    public void updateEquipment(Equipment equipment);
    public void deleteEquipment(long equipmentNum);
}
