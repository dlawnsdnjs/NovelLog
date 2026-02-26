package com.example.novelcharacter.domain.Episode.entity;

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
@Table(name="Episode")
public class Episode {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="novelNum", referencedColumnName = "novelNum", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Novel novel;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="episodeNum")
    private long episodeNum;

    @Column(name="episodeTitle", nullable=false)
    private String episodeTitle;

    @Column(name="episodeSummary")
    private String episodeSummary;

    @Column(name="orderIndex")
    private long orderIndex;
}
