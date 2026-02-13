package com.example.novelcharacter.domain.Episode.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EpisodeCharacterId implements Serializable {
    private long episodeNum;
    private long characterNum;

}
