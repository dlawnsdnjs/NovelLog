package com.example.novelcharacter.dto.Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PostDTO {
    private long postId;
    private long boardId;
    private String postTitle;
    private String content;
    private long uuid;
    private Date writeDate;
}
