package com.example.novelcharacter.domain.Equipment.entity;

import com.example.novelcharacter.domain.Stat.entity.Stat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name="EquipmentStat")
public class EquipmentStat {
    @EmbeddedId
    private EquipmentStatId id;

    @MapsId("equipmentNum")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="equipmentNum")
    private Equipment equipment;

    @MapsId("statCode")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="statCode")
    private Stat stat;

    @Column("statType")
    private int statType;


    @Column(name="value")
    private long value;
}
