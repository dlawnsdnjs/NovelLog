package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Board.PostDTO;
import com.example.novelcharacter.dto.Board.PostDataDTO;
import com.example.novelcharacter.dto.Board.PostResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    public void insertPost(PostDTO postDTO);
    public List<PostDataDTO> selectPostsByBoard(long boardId, int page);
    public List<PostDataDTO> selectPostsByUuid(long uuid, long boardId, int page);
    public List<PostDataDTO> selectPostsByUserName(String userName, long boardId, int page);
    public long selectPostCountByBoard(long boardId);
    public long selectPostCountByUuid(long boardId, long uuid);
    public long selectPostCountByUserName(long boardId, String userName);
    public PostResponseDTO selectPostById(long postId);
    public void updatePost(PostDTO postDTO);
    public void deletePost(long postId);
}
