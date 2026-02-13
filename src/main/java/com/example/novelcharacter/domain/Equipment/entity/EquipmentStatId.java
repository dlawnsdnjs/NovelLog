package com.example.novelcharacter.domain.Equipment.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EquipmentStatId implements Serializable {
    private long equipmentNum;
    private long statCode;
    private int statType;
}
