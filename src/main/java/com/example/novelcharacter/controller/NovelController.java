package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.Novel.NovelDTO;
import com.example.novelcharacter.dto.Novel.NovelWithFavoriteDTO;
import com.example.novelcharacter.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;

/**
 * 소설 관련 기능을 처리하는 컨트롤러 클래스.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>소설 생성 (insertNovel)</li>
 *     <li>즐겨찾는 소설 설정 (setFavoriteNovel)</li>
 *     <li>특정 소설 조회 (getNovel)</li>
 *     <li>사용자의 소설 목록 조회 (novelList)</li>
 *     <li>소설 삭제 (deleteNovel)</li>
 * </ul>
 * <p>
 * 모든 요청은 JWT 토큰을 기반으로 사용자의 UUID를 추출하여 인증 및 권한 검사를 수행합니다.
 */
@RestController
public class NovelController {
    private final NovelService novelService;
    private final JWTUtil jwtUtil;

    /**
     * NovelService 및 JWTUtil을 주입받는 생성자
     *
     * @param novelService 소설 관련 비즈니스 로직을 처리하는 서비스
     * @param jwtUtil JWT 토큰에서 사용자 정보를 추출하는 유틸리티
     */
    @Autowired
    public NovelController(NovelService novelService, JWTUtil jwtUtil){
        this.novelService = novelService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * [POST] /insertNovel
     * 새로운 소설을 생성하는 엔드포인트.
     *
     * @param access  헤더에 포함된 JWT Access Token
     * @param payload 요청 본문에 포함된 소설 제목 정보 (key: "title")
     * @return 생성된 소설 정보 (즐겨찾기 여부 포함)
     *
     * 처리 과정:
     * <ol>
     *     <li>JWT에서 사용자 UUID 추출</li>
     *     <li>제목을 기반으로 새 소설 등록</li>
     *     <li>등록된 소설 DTO 반환</li>
     * </ol>
     */
    @PostMapping("/insertNovel")
    public NovelWithFavoriteDTO insertNovel(
            @RequestHeader("access") String access,
            @RequestBody Map<String, String> payload) {

        long uuid = jwtUtil.getUuid(access);
        String title = payload.get("title");

        return novelService.insertNovel(title, uuid);
    }

    /**
     * [POST] /setFavoriteNovel
     * 사용자가 즐겨찾는 소설을 설정하는 엔드포인트.
     *
     * @param access  JWT Access Token
     * @param payload 요청 본문에 포함된 소설 번호 (key: "novelNum")
     *
     * 처리 과정:
     * <ol>
     *     <li>JWT에서 UUID 추출</li>
     *     <li>해당 소설을 즐겨찾기 목록에 등록</li>
     * </ol>
     */
    @PostMapping("/setFavoriteNovel")
    public void setFavoriteNovel(@RequestHeader("access") String access, @RequestBody Map<String, Long> payload){
        long uuid = jwtUtil.getUuid(access);
        novelService.setFavoriteNovel(payload.get("novelNum"), uuid);
    }

    /**
     * [GET] /novel/{novelNum}
     * 특정 소설의 상세 정보를 조회합니다.
     *
     * @param access   JWT Access Token
     * @param novelNum 조회할 소설 번호 (PathVariable)
     * @return NovelDTO : 조회 성공 시 소설 정보 반환
     * @throws NoPermissionException 권한이 없을 경우 예외 발생 (전역 예외 처리기에서 403 응답)
     *
     * 처리 과정:
     * <ol>
     *     <li>JWT에서 사용자 UUID를 추출합니다.</li>
     *     <li>서비스 계층에서 사용자의 소설 접근 권한을 검증합니다.</li>
     *     <li>권한이 없을 경우 {@link com.example.novelcharacter.ExceptionHandler.GlobalExceptionHandler}에서
     *         예외를 처리하고 403 FORBIDDEN 응답을 반환합니다.</li>
     *     <li>권한이 유효하면 소설 데이터를 조회하여 반환합니다.</li>
     * </ol>
     */
    @GetMapping("/getNovel/{novelNum}")
    public NovelDTO getNovel(@RequestHeader("access") String access,
                             @PathVariable("novelNum") long novelNum) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        return novelService.selectNovelOne(novelNum, uuid);
    }

    /**
     * [GET] /novelList
     * 로그인한 사용자가 소유한 모든 소설 목록을 조회.
     *
     * @param access JWT Access Token
     * @return 사용자의 모든 소설 목록 (즐겨찾기 여부 포함)
     *
     * 처리 과정:
     * <ol>
     *     <li>JWT에서 UUID 추출</li>
     *     <li>해당 사용자가 작성한 모든 소설 조회</li>
     * </ol>
     */
    @GetMapping("/novelList")
    public List<NovelWithFavoriteDTO> novelList(
            @RequestHeader("access") String access) {

        long uuid = jwtUtil.getUuid(access);

        return novelService.selectAllNovel(uuid);
    }

    /**
     * [POST] /deleteNovel
     * 소설을 삭제하는 엔드포인트.
     *
     * @param access  JWT Access Token
     * @param payload 요청 본문에 포함된 소설 번호 (key: "novelNum")
     * @return HTTP 204 No Content 응답
     *
     * 처리 과정:
     * <ol>
     *     <li>소설 번호를 받아 DB에서 삭제</li>
     *     <li>삭제 후 본문 없는 응답 반환</li>
     * </ol>
     */
    @PostMapping("/deleteNovel")
    public ResponseEntity<Void> deleteNovel(@RequestHeader String access, @RequestBody  Map<String, Long> payload) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        novelService.deleteNovel(payload.get("novelNum"), uuid);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
