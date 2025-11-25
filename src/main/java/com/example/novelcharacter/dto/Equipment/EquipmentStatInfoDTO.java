package com.example.novelcharacter.dto.Equipment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EquipmentStatInfoDTO {
    private String statName;
    private long value;
    private int type;
}
