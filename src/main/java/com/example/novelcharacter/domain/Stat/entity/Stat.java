package com.example.novelcharacter.domain.Stat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Stat")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="statCode")
    private long statCode;

    @Column(name="statName", nullable = false)
    private String statName;
}
