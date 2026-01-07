package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.Character.CharacterDTO;
import com.example.novelcharacter.dto.Episode.CharacterRequestDataDTO;
import com.example.novelcharacter.dto.Episode.CharacterResponseDataDTO;
import com.example.novelcharacter.dto.Episode.EpisodeCharacterDTO;
import com.example.novelcharacter.service.CharacterService;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;

/**
 * 회차별 캐릭터 상태(스탯 및 데이터)를 관리하는 REST 컨트롤러입니다.
 *
 * <p>JWT 기반 인증을 사용하여, 특정 회차에 등장하는 캐릭터들의 상태를 추가하거나 조회하는 기능을 제공합니다.</p>
 */
@RestController
@RequestMapping("/episode")
public class CharacterStatusController {

    private final JWTUtil jwtUtil;
    private final CharacterService characterService;

    /**
     * CharacterStatusController 생성자입니다.
     *
     * @param jwtUtil JWT 토큰에서 사용자 식별 정보를 추출하는 유틸리티 클래스
     * @param characterService 캐릭터 관련 비즈니스 로직을 처리하는 서비스 클래스
     */
    public CharacterStatusController(JWTUtil jwtUtil, CharacterService characterService) {
        this.jwtUtil = jwtUtil;
        this.characterService = characterService;
    }

    /**
     * 특정 회차에 캐릭터 상태 데이터를 추가합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param characterRequestDataDTO 추가할 캐릭터 상태 데이터 (캐릭터 번호, 회차 번호, 스탯 목록 등 포함)
     * @throws NoPermissionException 사용자가 해당 회차 또는 캐릭터에 접근할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /episode/addCharacterStatus
     * Header: access=JWT_TOKEN
     * Body:
     * {
     *   "episodeNum": 5,
     *   "characterNum": 12,
     *   "stats": [
     *     {"statName": "힘", "statValue": 10},
     *     {"statName": "지능", "statValue": 8}
     *   ]
     * }
     * </pre>
     *
     * <p>성공 시 HTTP 200 OK를 반환하며, 별도의 응답 본문은 없습니다.</p>
     */
    @PostMapping("/addCharacterStatus")
    public void addCharacterStatus(@RequestHeader("Access") String access, @RequestBody CharacterRequestDataDTO characterRequestDataDTO)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        characterService.addCharacterData(characterRequestDataDTO, uuid);
    }

    /**
     * 특정 회차에 등장하는 캐릭터 목록을 조회합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param payload 회차 번호를 포함한 요청 본문 (예: {"episodeNum": 1})
     * @return 해당 회차에 등장하는 캐릭터 목록
     * @throws NoPermissionException 사용자가 해당 회차에 접근할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /episode/characters
     * Header: access=JWT_TOKEN
     * Body: {"episodeNum": 3}
     * </pre>
     *
     * <p><b>응답 예시:</b></p>
     * <pre>
     * [
     *   {"characterNum": 1, "characterName": "루크"},
     *   {"characterNum": 2, "characterName": "엘라"}
     * ]
     * </pre>
     */
    @PostMapping("/characters")
    public List<CharacterDTO> selectEpisodeCharacters(@RequestHeader("Access") String access, @RequestBody Map<String, Long> payload)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        long episodeNum = payload.get("episodeNum");
        return characterService.selectCharactersByEpisode(episodeNum, uuid);
    }

    /**
     * 특정 회차 내 캐릭터의 상세 상태(스탯, 장비 등)를 조회합니다.
     *
     * @param access JWT Access Token (헤더)
     * @param episodeCharacterDTO 캐릭터 번호 및 회차 번호를 포함한 요청 데이터
     * @return 해당 캐릭터의 상태 및 스탯 정보를 포함한 응답 데이터
     * @throws NoPermissionException 사용자가 해당 캐릭터 데이터를 조회할 권한이 없을 경우 발생
     *
     * <p><b>요청 예시:</b></p>
     * <pre>
     * POST /episode/characterStatus
     * Header: access=JWT_TOKEN
     * Body:
     * {
     *   "episodeNum": 5,
     *   "characterNum": 12
     * }
     * </pre>
     *
     * <p><b>응답 예시:</b></p>
     * <pre>
     * {
     *   "character": {"characterNum": 12, "characterName": "아르테미스"},
     *   "stats": [
     *     {"statName": "힘", "value": 15},
     *     {"statName": "민첩", "value": 12}
     *   ]
     * }
     * </pre>
     */
    @PostMapping("/characterStatus")
    public CharacterResponseDataDTO selectCharacterStats(@RequestHeader("Access") String access, @RequestBody EpisodeCharacterDTO episodeCharacterDTO)
            throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        return characterService.selectCharacterData(episodeCharacterDTO, uuid);
    }

    @PostMapping("/characterStatus/recent")
    public CharacterResponseDataDTO selectRecentCharacterStats(@RequestHeader("Access") String access, @RequestBody EpisodeCharacterDTO episodeCharacterDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        return characterService.selectRecentCharacterData(episodeCharacterDTO, uuid);
    }

    @PostMapping("/deleteCharacterStatus")
    public void deleteCharacterStatus(@RequestHeader("Access") String access, @RequestBody EpisodeCharacterDTO episodeCharacterDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        characterService.deleteEpisodeCharacter(episodeCharacterDTO, uuid);
    }
}
