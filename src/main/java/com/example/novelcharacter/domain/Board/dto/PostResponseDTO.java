package com.example.novelcharacter.domain.Board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PostResponseDTO {
    private long postId;
    private String postTitle;
    private String userName;
    private String content;
}
