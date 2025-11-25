package com.example.novelcharacter.dto.Equipment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EquipmentStatDTO {
    private long equipmentNum;
    private long statCode;
    private int statType; // 실제 테이블은 int로 돼있음 enum으로 대체하던지 해야할듯
    private long value;
}
