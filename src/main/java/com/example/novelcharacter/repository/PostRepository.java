package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Board.dto.PostDataDTO;
import com.example.novelcharacter.domain.Board.dto.PostResponseDTO;
import com.example.novelcharacter.domain.Board.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select new com.example.novelcharacter.domain.Board.dto.PostDataDTO(p.postId, p.postTitle, u.userName) " +
            "from Post p join p.user u where p.board.boardId = :boardId " +
            "order by p.postId desc")
    public List<PostDataDTO> selectPostsByBoard(@Param("boardId") long boardId, Pageable pageable);

    @Query("select new com.example.novelcharacter.domain.Board.dto.PostDataDTO(p.postId, p.postTitle, u.userName) " +
            "from Post p join p.user u where p.board.boardId = :boardId and u.userName like concat('%', :userName, '%') " +
            "order by p.postId desc ")
    public List<PostDataDTO> selectPostsByUserName(@Param("userName") String userName,@Param("boardId") long boardId, Pageable pageable);

    @Query("select new com.example.novelcharacter.domain.Board.dto.PostDataDTO(p.postId, p.postTitle, u.userName) " +
            "from Post p join p.user u where p.board.boardId = :boardId and u.uuid = :uuid " +
            "order by p.postId desc ")
    public List<PostDataDTO> selectPostsByUuid(@Param("uuid") long uuid,@Param("boardId") long boardId, Pageable pageable);

    public long countPostByBoard_BoardId(long boardId);

    @Query("select count(p) from Post p where p.board.boardId = :boardId and p.user.uuid = :uuid")
    public long selectPostCountByUuid(@Param("boardId") long boardId,@Param("uuid") long uuid);

    @Query("select count(p) from Post p join p.user u where p.board.boardId = :boardId and u.userName = :userName")
    public long selectPostCountByUserName(@Param("boardId") long boardId,@Param("userName") String userName);

    @Query("select new com.example.novelcharacter.domain.Board.dto.PostResponseDTO(p.postId, p.postTitle, u.userName, p.content) " +
            "from Post p join p.user u where p.postId = :postId")
    public PostResponseDTO selectPostById(@Param("postId") long postId);

}
