package com.example.novelcharacter.domain.Board.entity;

import com.example.novelcharacter.domain.User.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="boardId")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private BoardCategory board;

    @Column(name="postTitle", nullable=false)
    private String postTitle;

    @Column(name="content", nullable=false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uuid")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private User user;

    @Column(name="writeDate")
    private Date writeDate;
}
