package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Board.entity.Post;
import com.example.novelcharacter.domain.Board.dto.PostDataDTO;
import com.example.novelcharacter.domain.Board.dto.PostResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    public void insertPost(Post post);
    public List<PostDataDTO> selectPostsByBoard(long boardId, int page);
    public List<PostDataDTO> selectPostsByUuid(long uuid, long boardId, int page);
    public List<PostDataDTO> selectPostsByUserName(String userName, long boardId, int page);
    public long selectPostCountByBoard(long boardId);
    public long selectPostCountByUuid(long boardId, long uuid);
    public long selectPostCountByUserName(long boardId, String userName);
    public PostResponseDTO selectPostById(long postId);
    public void updatePost(Post post);
    public void deletePost(long postId);
}
