package com.example.novelcharacter.domain.Board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="BoardCategory")
public class BoardCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="boardId")
    private long boardId;

    @Column(name="boardName", nullable = false)
    private String boardName;
}
