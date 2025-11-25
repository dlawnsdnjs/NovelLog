package com.example.novelcharacter.dto.Equipment;

import lombok.Data;

import java.util.List;

@Data
public class EquipmentDataDTO {
    private EquipmentDTO equipment;

    private List<EquipmentStatInfoDTO> equipmentStats;
}

