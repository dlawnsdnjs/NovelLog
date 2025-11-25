package com.example.novelcharacter.dto.Novel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NovelWithFavoriteDTO {

    private long novelNum;
    private String novelTitle;
    private long uuid;
    private boolean isFavorite;
}
