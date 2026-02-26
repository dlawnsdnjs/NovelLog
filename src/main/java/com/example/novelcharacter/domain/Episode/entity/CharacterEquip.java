package com.example.novelcharacter.domain.Episode.entity;

import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name="CharacterEquip")
public class CharacterEquip {
    @EmbeddedId
    private CharacterEquipId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="episodeNum", referencedColumnName="episodeNum", insertable=false, updatable=false),
            @JoinColumn(name="characterNum", referencedColumnName="characterNum", insertable=false, updatable=false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EpisodeCharacter episodeCharacter;

    @MapsId("equipmentNum")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="equipmentNum", referencedColumnName = "equipmentNum", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipment equipment;
}
