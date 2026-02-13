package com.example.novelcharacter.domain;

import com.example.novelcharacter.domain.User.entity.User;
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
@Table(name="Favorite")
public class Favorite {
    @EmbeddedId
    private FavoriteId id;

    @MapsId("uuid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uuid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @MapsId("targetType")
    @Column(name="targetType", nullable = false)
    private String targetType;

    @MapsId("targetId")
    @Column(name="targetId", nullable = false)
    private long targetId;
}
