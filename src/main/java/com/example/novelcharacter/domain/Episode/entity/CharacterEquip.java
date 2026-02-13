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

    @MapsId("episodeCharacterId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="episodeNum"),
            @JoinColumn(name="characterNum")
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EpisodeCharacter episodeCharacter;

    @MapsId("equipmentNum")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="equipmentNum")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipment equipment;
}
