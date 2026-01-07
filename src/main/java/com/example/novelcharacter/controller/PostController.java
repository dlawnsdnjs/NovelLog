package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.Board.BoardCategoryDTO;
import com.example.novelcharacter.dto.Board.PostDTO;
import com.example.novelcharacter.dto.Board.PostPageResponseDTO;
import com.example.novelcharacter.dto.Board.PostResponseDTO;
import com.example.novelcharacter.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;

/**
 * 게시판 및 게시글 관련 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>게시판 목록 조회, 게시글 조회, 작성, 수정, 삭제 기능을 제공합니다.
 * 또한 JWT 토큰을 이용하여 사용자 인증 및 권한 검증을 수행합니다.</p>
 *
 * <ul>
 *     <li>게시판 목록 조회: {@link #selectAllBoard()}</li>
 *     <li>게시글 목록 조회: {@link #selectPostsByBoardId(long, int)}</li>
 *     <li>특정 사용자 게시글 조회: {@link #selectPostsByUserName(long, String, int)}</li>
 *     <li>내 게시글 목록 조회: {@link #selectMyPostsByBoardId(String, long, int)}</li>
 *     <li>게시글 단건 조회: {@link #selectPostById(long)}</li>
 *     <li>게시글 작성, 수정, 삭제: {@link #postAdd(String, PostDTO)}, {@link #updatePost(String, PostDTO)}, {@link #deletePost(String, PostDTO)}</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JWTUtil jwtUtil;

    /**
     * 전체 게시판 목록을 조회합니다.
     *
     * @return 게시판 카테고리 목록 ({@link BoardCategoryDTO})
     */
    @GetMapping("/board")
    public List<BoardCategoryDTO> selectAllBoard() {
        return postService.selectAllBoardCategory();
    }

    /**
     * 특정 게시판에 속한 게시글 목록을 페이지 단위로 조회합니다.
     *
     * @param boardId 게시판 ID
     * @param page    페이지 번호
     * @return 게시글 목록 및 페이지 정보 ({@link PostPageResponseDTO})
     */
    @GetMapping("/post/boardId={boardId}/page={page}")
    public PostPageResponseDTO selectPostsByBoardId(@PathVariable("boardId") long boardId, @PathVariable("page") int page) {
        return postService.selectPostsByBoard(boardId, page);
    }

    /**
     * 특정 사용자가 작성한 게시글 목록을 조회합니다.
     *
     * @param boardId  게시판 ID
     * @param userName 작성자 이름
     * @param page     페이지 번호
     * @return 게시글 목록 및 페이지 정보 ({@link PostPageResponseDTO})
     */
    @GetMapping("/post/boardId={boardId}/userName={userName}/page={page}")
    public PostPageResponseDTO selectPostsByUserName(@PathVariable("boardId") long boardId,
                                                     @PathVariable String userName,
                                                     @PathVariable("page") int page) {
        return postService.selectPostsByUsername(userName, boardId, page);
    }

    /**
     * JWT 토큰을 통해 인증된 사용자의 게시글 목록을 조회합니다.
     *
     * @param access  요청 헤더의 Access 토큰
     * @param boardId 게시판 ID
     * @param page    페이지 번호
     * @return 사용자가 작성한 게시글 목록 ({@link PostPageResponseDTO})
     */
    @GetMapping("/myPost/boardId={boardId}/page={page}")
    public PostPageResponseDTO selectMyPostsByBoardId(@RequestHeader("Access") String access,
                                                      @PathVariable("boardId") long boardId,
                                                      @PathVariable("page") int page) {
        long uuid = jwtUtil.getUuid(access);
        return postService.selectPostsByUuid(uuid, boardId, page);
    }

    /**
     * 특정 게시글의 상세 정보를 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 게시글 상세 정보 ({@link PostResponseDTO})
     */
    @GetMapping("/post/postId={postId}")
    public PostResponseDTO selectPostById(@PathVariable("postId") long postId) {
        return postService.selectPostById(postId);
    }

    /**
     * 새 게시글을 작성합니다.
     *
     * @param access  요청 헤더의 Access 토큰 (작성자 식별용)
     * @param postDTO 게시글 데이터 ({@link PostDTO})
     * @throws NoPermissionException 권한이 없는 사용자가 요청한 경우
     */
    @PostMapping("/postAdd")
    public void postAdd(@RequestHeader("Access") String access,
                        @RequestBody PostDTO postDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        String role = jwtUtil.getRole(access);
        postService.insertPost(postDTO, uuid, role);
    }

    /**
     * 기존 게시글을 수정합니다.
     *
     * @param access  요청 헤더의 Access 토큰 (작성자 검증용)
     * @param postDTO 수정할 게시글 데이터 ({@link PostDTO})
     * @throws NoPermissionException 다른 사용자의 게시글을 수정하려는 경우
     */
    @PostMapping("/postUpdate")
    public void updatePost(@RequestHeader("Access") String access,
                           @RequestBody PostDTO postDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        postService.updatePost(postDTO, uuid);
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param access  요청 헤더의 Access 토큰 (작성자 검증용)
     * @param postDTO 삭제할 게시글 데이터 ({@link PostDTO})
     * @throws NoPermissionException 다른 사용자의 게시글을 삭제하려는 경우
     */
    @PostMapping("/postDelete")
    public void deletePost(@RequestHeader("Access") String access,
                           @RequestBody PostDTO postDTO) throws NoPermissionException {
        String userName = jwtUtil.getUsername(access);
        postService.deletePost(postDTO, userName);
    }
}
