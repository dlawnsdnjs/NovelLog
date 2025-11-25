package com.example.novelcharacter.dto.Equipment;

import com.example.novelcharacter.dto.Stat.StatRequestDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EquipmentStatRequestDTO {
    private StatRequestDTO stat;
    private int type;
}
