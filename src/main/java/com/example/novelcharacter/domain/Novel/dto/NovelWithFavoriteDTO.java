package com.example.novelcharacter.domain.Novel.dto;

import com.example.novelcharacter.domain.Novel.entity.Novel;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NovelWithFavoriteDTO {

    private long novelNum;
    private String novelTitle;
    private long uuid;
    private boolean isFavorite;
}
