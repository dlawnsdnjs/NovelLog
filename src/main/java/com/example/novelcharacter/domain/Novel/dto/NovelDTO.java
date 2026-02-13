package com.example.novelcharacter.domain.Novel.dto;

import com.example.novelcharacter.domain.Novel.entity.Novel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NovelDTO {
    private long novelNum;
    private String novelTitle;
    private long uuid;

    public static NovelDTO from(Novel novel) {
        return NovelDTO.builder()
                .novelNum(novel.getNovelNum())
                .novelTitle(novel.getNovelTitle())
                .uuid(novel.getUser().getUuid())
                .build();
    }
}
