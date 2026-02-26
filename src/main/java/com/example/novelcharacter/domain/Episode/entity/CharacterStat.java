package com.example.novelcharacter.domain.Episode.entity;

import com.example.novelcharacter.domain.Stat.entity.Stat;
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
@Table(name="CharacterStat")
public class CharacterStat {
    @EmbeddedId
    private CharacterStatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="episodeNum", referencedColumnName="episodeNum", insertable=false, updatable=false),
            @JoinColumn(name="characterNum", referencedColumnName="characterNum", insertable=false, updatable=false)
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EpisodeCharacter episodeCharacter;

    @MapsId("statCode")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="statCode", referencedColumnName = "statCode", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Stat stat;

    @Column(name="value")
    private long value;
}
