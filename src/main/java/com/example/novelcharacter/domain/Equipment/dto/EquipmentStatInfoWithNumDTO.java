package com.example.novelcharacter.domain.Equipment.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStatInfoWithNumDTO {
    private long equipmentNum;
    private String statName;
    private long value;
    private int type;
}
