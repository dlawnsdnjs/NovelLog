package com.example.novelcharacter.domain.Board.dto;

import com.example.novelcharacter.domain.Board.entity.Post;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private long postId;
    private long boardId;
    private String postTitle;
    private String content;
    private long uuid;
    private Date writeDate;

    public static PostDTO fromPost(Post post) {
        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setBoardId(post.getBoard().getBoardId());
        dto.setPostTitle(post.getPostTitle());
        dto.setContent(post.getContent());
        dto.setWriteDate(post.getWriteDate());
        dto.setUuid(post.getUser().getUuid());
        return dto;
    }
}
