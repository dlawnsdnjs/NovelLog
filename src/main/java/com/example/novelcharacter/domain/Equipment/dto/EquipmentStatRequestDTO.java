package com.example.novelcharacter.domain.Equipment.dto;

import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
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
