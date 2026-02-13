package com.example.novelcharacter.domain.Equipment.dto;

import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipmentDTO {
    private long novelNum;
    private long equipmentNum;
    private String equipmentName;
    private String inform;

    public static EquipmentDTO from(Equipment equipment) {
        return EquipmentDTO.builder()
                .novelNum(equipment.getNovel().getNovelNum())
                .equipmentNum(equipment.getEquipmentNum())
                .equipmentName(equipment.getEquipmentName())
                .inform(equipment.getInform())
                .build();
    }
}
