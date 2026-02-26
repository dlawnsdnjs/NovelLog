package com.example.novelcharacter.domain.Equipment.entity;

import com.example.novelcharacter.domain.Novel.entity.Novel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name="Equipment")
public class Equipment {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelNum", referencedColumnName = "novelNum", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Novel novel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="equipmentNum")
    private long equipmentNum;

    @Column(name="equipmentName", nullable=false)
    private String equipmentName;

    @Column(name="inform")
    private String inform;

    @OneToMany(mappedBy = "equipment")
    private List<EquipmentStat> stats = new ArrayList<>();
}
