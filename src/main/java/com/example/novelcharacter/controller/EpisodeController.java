package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.Episode.EpisodeDTO;
import com.example.novelcharacter.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;

/**
 * 소설 회차(Episode) 관련 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>소설의 회차 목록 조회, 회차 추가 및 삭제 기능을 제공합니다.<br>
 * 모든 요청은 JWT 인증을 기반으로 하며, 토큰의 UUID를 통해 사용자 권한을 검증합니다.</p>
 */
@RestController
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;
    private final JWTUtil jwtUtil;


    /**
     * 특정 소설의 모든 회차 목록을 조회합니다.
     *
     * @param access  JWT Access Token (HTTP Header)
     * @param payload 요청 본문에 포함된 소설 번호 (예: {"novelNum": 1})
     * @return 지정된 소설의 회차 목록
     * @throws NoPermissionException 사용자가 해당 소설의 회차에 접근할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /allEpisode
     * Header: access=JWT_TOKEN
     * Body:
     * {
     *   "novelNum": 3
     * }
     * </pre>
     *
     * <p><b>응답 예시:</b></p>
     * <pre>
     * [
     *   {"episodeNum": 1, "episodeTitle": "1화 - 운명의 만남"},
     *   {"episodeNum": 2, "episodeTitle": "2화 - 첫 번째 전투"}
     * ]
     * </pre>
     */
    @PostMapping("/allEpisode")
    public List<EpisodeDTO> getAllEpisode(@RequestHeader("Access") String access, @RequestBody Map<String, Long> payload)
            throws NoPermissionException {

        long uuid = jwtUtil.getUuid(access);
        long novelNum = payload.get("novelNum");

        return episodeService.selectAllEpisode(novelNum, uuid);
    }

    /**
     * 새로운 회차를 추가합니다.
     *
     * @param access JWT Access Token (HTTP Header)
     * @param episodeDTO 추가할 회차 정보 (제목, 내용, 소설 번호 등 포함)
     * @return 등록된 회차 정보
     * @throws NoPermissionException 사용자가 해당 소설에 회차를 추가할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /addEpisode
     * Header: access=JWT_TOKEN
     * Body:
     * {
     *   "novelNum": 3,
     *   "episodeTitle": "3화 - 그림자의 추격",
     *   "content": "주인공은 어둠 속에서 적을 마주하게 된다..."
     * }
     * </pre>
     *
     * <p><b>응답 예시:</b></p>
     * <pre>
     * {
     *   "episodeNum": 5,
     *   "episodeTitle": "3화 - 그림자의 추격",
     *   "novelNum": 3
     * }
     * </pre>
     */
    @PostMapping("/addEpisode")
    public EpisodeDTO addEpisode(@RequestHeader("Access") String access, @Valid @RequestBody EpisodeDTO episodeDTO)
            throws NoPermissionException {

        long uuid = jwtUtil.getUuid(access);
        episodeService.insertEpisode(episodeDTO, uuid);

        return episodeDTO;
    }

    /**
     * 특정 회차를 삭제합니다.
     *
     * @param access JWT Access Token (HTTP Header)
     * @param episodeDTO 삭제할 회차 정보 (회차 번호 포함)
     * @return HTTP 204 No Content (성공적으로 삭제된 경우)
     * @throws NoPermissionException 사용자가 해당 회차를 삭제할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /deleteEpisode
     * Header: access=JWT_TOKEN
     * Body:
     * {
     *   "episodeNum": 5
     * }
     * </pre>
     *
     * <p><b>응답:</b></p>
     * <pre>
     * HTTP 204 No Content
     * </pre>
     */
    @PostMapping("/deleteEpisode")
    public ResponseEntity<Void> deleteEpisode(@RequestHeader("Access") String access, @RequestBody EpisodeDTO episodeDTO)
            throws NoPermissionException {

        long uuid = jwtUtil.getUuid(access);
        episodeService.deleteEpisode(episodeDTO.getEpisodeNum(), uuid);

        return ResponseEntity.noContent().build();
    }
}
