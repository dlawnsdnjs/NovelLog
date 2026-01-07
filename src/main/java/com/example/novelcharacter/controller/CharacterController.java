package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.Character.CharacterDTO;
import com.example.novelcharacter.service.CharacterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;

/**
 * 캐릭터 관련 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>JWT 인증을 기반으로 사용자의 소설, 회차 내 캐릭터 데이터를 조회, 추가, 삭제하는 기능을 제공합니다.</p>
 */
@RequiredArgsConstructor
@RestController
public class CharacterController {

    private final CharacterService characterService;
    private final JWTUtil jwtUtil;

    /**
     * 특정 회차에 포함된 캐릭터 목록을 조회합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param episodeNum 조회할 회차 번호
     * @return 해당 회차에 포함된 캐릭터 목록
     * @throws NoPermissionException 사용자가 해당 회차에 접근할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /episodeCharacters
     * Header: access=JWT_TOKEN
     * Body: 1
     * </pre>
     */
    @PostMapping("/episodeCharacters")
    public List<CharacterDTO> episodeCharacter(@RequestHeader("Access") String access, @RequestBody long episodeNum)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        return characterService.selectCharactersByEpisode(episodeNum, uuid);
    }

    /**
     * 특정 소설에 속한 모든 캐릭터 목록을 조회합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param payload 소설 번호가 포함된 요청 본문 (예: {"novelNum": 1})
     * @return 해당 소설에 속한 캐릭터 목록
     * @throws NoPermissionException 사용자가 해당 소설에 접근할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /novelCharacters
     * Header: access=JWT_TOKEN
     * Body: {"novelNum": 3}
     * </pre>
     */
    @PostMapping("/novelCharacters")
    public List<CharacterDTO> novelCharacters(@RequestHeader("Access") String access, @RequestBody Map<String, Long> payload)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        long novelNum = payload.get("novelNum");
        return characterService.selectCharacterList(novelNum, uuid);
    }

    /**
     * 새로운 캐릭터를 추가합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param characterDTO 추가할 캐릭터 데이터 (이름, 직업, 설명 등)
     * @return 추가된 캐릭터 정보
     * @throws NoPermissionException 사용자가 해당 소설에 캐릭터를 추가할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /addCharacter
     * Header: access=JWT_TOKEN
     * Body: {
     *   "novelNum": 1,
     *   "characterName": "아르테미스",
     *   "role": "주인공"
     * }
     * </pre>
     */
    @PostMapping("/addCharacter")
    public CharacterDTO addCharacter(@RequestHeader("Access") String access, @Valid @RequestBody CharacterDTO characterDTO)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        characterService.insertCharacter(characterDTO, uuid);
        return characterDTO;
    }

    /**
     * 특정 캐릭터를 삭제합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param characterDTO 삭제할 캐릭터 정보 (characterNum 또는 novelNum 등 포함)
     * @return 삭제 성공 시 204 No Content 응답
     * @throws NoPermissionException 사용자가 해당 캐릭터를 삭제할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /deleteCharacter
     * Header: access=JWT_TOKEN
     * Body: {"characterNum": 5}
     * </pre>
     */
    @PostMapping("/deleteCharacter")
    public ResponseEntity<Void> deleteEpisode(@RequestHeader("Access") String access, @RequestBody CharacterDTO characterDTO)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        characterService.deleteCharacter(characterDTO, uuid);
        return ResponseEntity.noContent().build();
    }
}
