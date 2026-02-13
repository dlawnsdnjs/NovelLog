package com.example.novelcharacter.domain.Episode.entity;

import com.example.novelcharacter.domain.Character.entity.Character;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="EpisodeCharacter")
public class EpisodeCharacter {
    @EmbeddedId
    private EpisodeCharacterId id;

    @MapsId("episodeNum")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="episodeNum")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Episode episode;

    @MapsId("characterNum")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="characterNum")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Character character;
}
