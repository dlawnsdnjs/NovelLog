package com.example.novelcharacter.domain.Episode.entity;

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
public class CharacterEquipId implements Serializable {
    private EpisodeCharacterId episodeCharacterId;
    private long equipmentNum;
}
