package com.example.novelcharacter.domain.Character.entity;

import com.example.novelcharacter.domain.Novel.entity.Novel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="`Character`")
public class Character {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelNum", referencedColumnName = "novelNum", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Novel novel;

    @Column(name="characterName", nullable = false)
    private String characterName;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="characterNum")
    private long characterNum;
}
