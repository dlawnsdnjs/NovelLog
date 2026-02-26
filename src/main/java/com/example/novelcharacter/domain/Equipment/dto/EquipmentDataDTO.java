package com.example.novelcharacter.domain.Equipment.dto;

import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import lombok.Data;

import java.util.List;

@Data
public class EquipmentDataDTO {
    private EquipmentDTO equipment;

    private List<EquipmentStatInfoDTO> equipmentStats;
}

