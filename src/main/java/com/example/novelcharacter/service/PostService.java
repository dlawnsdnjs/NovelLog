package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.*;
import com.example.novelcharacter.mapper.BoardCategoryMapper;
import com.example.novelcharacter.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;

/**
 * {@code PostService}는 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * 게시글 등록, 조회, 수정, 삭제 및 게시판 카테고리 조회를 담당하며,
 * 사용자 권한을 검증하여 허가되지 않은 접근을 차단합니다.
 * </p>
 */
@Service
public class PostService {
    private final PostMapper postMapper;
    private final BoardCategoryMapper boardCategoryMapper;

    /**
     * {@code PostService} 생성자.
     *
     * @param postMapper           게시글 데이터베이스 접근을 위한 매퍼
     * @param boardCategoryMapper  게시판 카테고리 데이터 접근을 위한 매퍼
     */
    @Autowired
    public PostService(PostMapper postMapper, BoardCategoryMapper boardCategoryMapper) {
        this.postMapper = postMapper;
        this.boardCategoryMapper = boardCategoryMapper;
    }

    /**
     * 게시글을 등록합니다.
     * <p>
     * 관리자가 아닌 사용자는 관리자 게시판(boardId = 0)에 글을 등록할 수 없습니다.
     * </p>
     *
     * @param postDTO 등록할 게시글 데이터
     * @param uuid    작성자 고유 식별자
     * @param role    작성자 권한 (예: ROLE_USER, ROLE_ADMIN)
     * @throws NoPermissionException 관리자가 아닌 사용자가 관리자 게시판에 글을 쓰려고 할 때 발생
     */
    public void insertPost(PostDTO postDTO, long uuid, String role) throws NoPermissionException {
        if (postDTO.getBoardId() == 0 && !role.equals("ROLE_ADMIN")) {
            throw new NoPermissionException("관리자 권한이 필요합니다.");
        }
        postDTO.setUuid(uuid);

        postMapper.insertPost(postDTO);
    }

    /**
     * 게시판 ID를 기준으로 게시글 목록을 페이징하여 조회합니다.
     *
     * @param boardId 게시판 ID
     * @param page    조회할 페이지 번호 (1부터 시작)
     * @return 게시글 목록과 총 개수를 포함한 {@link PostPageResponseDTO}
     */
    public PostPageResponseDTO selectPostsByBoard(long boardId, int page) {
        PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO();
        postPageResponseDTO.setData(postMapper.selectPostsByBoard(boardId, (page - 1) * 20));
        postPageResponseDTO.setTotalCount(postMapper.selectPostCountByBoard(boardId));
        return postPageResponseDTO;
    }

    /**
     * 특정 사용자가 작성한 게시글을 게시판 ID 기준으로 조회합니다.
     *
     * @param uuid    사용자 고유 식별자
     * @param boardId 게시판 ID
     * @param page    조회할 페이지 번호
     * @return 사용자가 작성한 게시글 목록과 총 개수를 포함한 {@link PostPageResponseDTO}
     */
    public PostPageResponseDTO selectPostsByUuid(long uuid, long boardId, int page) {
        PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO();
        postPageResponseDTO.setData(postMapper.selectPostsByUuid(uuid, boardId, (page - 1) * 20));
        postPageResponseDTO.setTotalCount(postMapper.selectPostCountByUuid(boardId, uuid));
        return postPageResponseDTO;
    }

    /**
     * 특정 사용자명으로 작성된 게시글을 조회합니다.
     *
     * @param userName 사용자 이름
     * @param boardId  게시판 ID
     * @param page     조회할 페이지 번호
     * @return 사용자명으로 검색된 게시글 목록과 총 개수를 포함한 {@link PostPageResponseDTO}
     */
    public PostPageResponseDTO selectPostsByUsername(String userName, long boardId, int page) {
        PostPageResponseDTO postPageResponseDTO = new PostPageResponseDTO();
        postPageResponseDTO.setData(postMapper.selectPostsByUserName(userName, boardId, (page - 1) * 20));
        postPageResponseDTO.setTotalCount(postMapper.selectPostCountByUserName(boardId, userName));
        return postPageResponseDTO;
    }

    /**
     * 게시글 ID로 게시글을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 조회된 게시글 정보 {@link PostResponseDTO}
     */
    public PostResponseDTO selectPostById(long postId) {
        return postMapper.selectPostById(postId);
    }

    /**
     * 게시글을 수정합니다.
     * <p>
     * 게시글 작성자만 수정이 가능하며, 다른 사용자가 접근 시 예외가 발생합니다.
     * </p>
     *
     * @param postDTO 수정할 게시글 데이터
     * @param uuid    수정 요청자 고유 식별자
     * @throws NoPermissionException 작성자가 아닌 사용자가 수정하려 할 때 발생
     */
    public void updatePost(PostDTO postDTO, long uuid) throws NoPermissionException {
        if (postDTO.getUuid() != uuid) {
            throw new NoPermissionException("작성자만 수정 가능합니다.");
        }
        postMapper.updatePost(postDTO);
    }

    /**
     * 게시글을 삭제합니다.
     * <p>
     * 게시글 작성자만 삭제할 수 있습니다.
     * </p>
     *
     * @param postDTO 삭제할 게시글 정보
     * @param userName    요청자 고유 식별자
     * @throws NoPermissionException 작성자가 아닌 사용자가 삭제하려 할 때 발생
     */
    public void deletePost(PostDTO postDTO, String userName) throws NoPermissionException {
        String postWriter = selectPostById(postDTO.getPostId()).getUserName();
        if (!postWriter.equals(userName)) {
            throw new NoPermissionException("작성자만 삭제 가능합니다.");
        }
        postMapper.deletePost(postDTO.getPostId());
    }

    /**
     * 모든 게시판 카테고리를 조회합니다.
     *
     * @return 게시판 카테고리 목록 {@link BoardCategoryDTO}
     */
    public List<BoardCategoryDTO> selectAllBoardCategory() {
        return boardCategoryMapper.selectAllBoardCategory();
    }
}
