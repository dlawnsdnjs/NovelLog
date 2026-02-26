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
    @JoinColumn(name="equipmentNum", referencedColumnName = "equipmentNum", nullable = false)
    private Equipment equipment;

    @MapsId("statCode")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="statCode", referencedColumnName = "statCode", nullable = false)
    private Stat stat;

    @Column(name="statType", insertable = false, updatable = false)
    private int statType;


    @Column(name="value")
    private long value;
}
