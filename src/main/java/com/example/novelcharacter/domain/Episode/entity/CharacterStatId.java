package com.example.novelcharacter.domain.Episode.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CharacterStatId implements Serializable {
    private EpisodeCharacterId episodeCharacterId;
    private long statCode;
}
