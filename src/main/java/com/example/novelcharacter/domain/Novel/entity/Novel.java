package com.example.novelcharacter.domain.Novel.entity;

import com.example.novelcharacter.domain.User.entity.User;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Novel")
public class Novel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="novelNum")
    private long novelNum;

    @Column(name="novelTitle", nullable=false)
    private String novelTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uuid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
