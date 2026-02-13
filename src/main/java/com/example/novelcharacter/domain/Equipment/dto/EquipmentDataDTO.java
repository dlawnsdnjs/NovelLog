package com.example.novelcharacter.domain.Equipment.dto;

import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import lombok.Data;

import java.util.List;

@Data
public class EquipmentDataDTO {
    private Equipment equipment;

    private List<EquipmentStatInfoDTO> equipmentStats;
}

