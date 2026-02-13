package com.example.novelcharacter.service;

import com.example.novelcharacter.component.StatCalculator;
import com.example.novelcharacter.domain.Character.entity.Character;
import com.example.novelcharacter.domain.Episode.dto.CharacterRequestDataDTO;
import com.example.novelcharacter.domain.Episode.dto.CharacterResponseDataDTO;
import com.example.novelcharacter.domain.Episode.entity.CharacterEquip;
import com.example.novelcharacter.domain.Episode.entity.CharacterStat;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentDataDTO;
import com.example.novelcharacter.domain.Stat.entity.Stat;
import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
import com.example.novelcharacter.mapper.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 캐릭터 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>소설, 에피소드, 장비, 스탯 등과 연동되어 캐릭터의 생성, 수정, 삭제 및
 * 스탯/장비 데이터의 관리 기능을 제공합니다.</p>
 *
 * @author
 */
@RequiredArgsConstructor
@Service
public class CharacterService {

    private final CharacterMapper characterMapper;
    private final EpisodeCharacterMapper episodeCharacterMapper;
    private final CharacterStatMapper characterStatMapper;
    private final CharacterEquipMapper characterEquipMapper;
    private final NovelService novelService;
    private final EpisodeService episodeService;
    private final EquipmentService equipmentService;
    private final StatService statService;
    private final StatCalculator statCalculator;


    /**
     * 새로운 캐릭터를 등록합니다.
     *
     * @param character 등록할 캐릭터 정보
     * @param uuid 소설 소유자 UUID
     * @throws NoPermissionException 소설의 소유자가 아닌 경우
     */
    public void insertCharacter(Character character, long uuid) throws NoPermissionException {
        novelService.checkOwner(character.getNovelNum(), uuid);
        characterMapper.insertCharacter(character);
    }

    /**
     * 캐릭터 번호로 캐릭터 정보를 조회합니다.
     *
     * @param characterNum 캐릭터 번호
     * @return 캐릭터 정보
     */
    public Character selectCharacter(long characterNum) {
        return characterMapper.selectCharacter(characterNum);
    }

    /**
     * 특정 사용자가 캐릭터의 소유자인지 확인합니다.
     *
     * @param uuid 사용자 UUID
     * @param characterNum 캐릭터 번호
     * @throws NoPermissionException 사용자가 캐릭터의 소유자가 아닌 경우
     */
    public void checkCharacterOwner(long uuid, long characterNum) throws NoPermissionException {
        if (characterMapper.checkCharacterOwner(uuid, characterNum) != 1) {
            throw new NoPermissionException("사용자가 작성한 등장인물이 아닙니다.");
        }
    }

    /**
     * 소설 내의 캐릭터 목록을 조회합니다.
     *
     * @param novelNum 소설 번호
     * @param uuid 소설 소유자 UUID
     * @return 캐릭터 목록
     * @throws NoPermissionException 소설의 소유자가 아닌 경우
     */
    public List<Character> selectCharacterList(long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return characterMapper.selectCharacterList(novelNum);
    }

    /**
     * 캐릭터 이름을 기준으로 검색합니다.
     *
     * @param novelNum 소설 번호
     * @param character 검색할 캐릭터명
     * @return 검색된 캐릭터 목록
     */
    public List<Character> searchCharacterList(long novelNum, String character) {
        return characterMapper.searchCharacterList(novelNum, character);
    }

    /**
     * 캐릭터 정보를 수정합니다.
     *
     * @param character 수정할 캐릭터 정보
     */
    public void updateCharacter(Character character) {
        characterMapper.updateCharacter(character);
    }

    /**
     * 캐릭터를 삭제합니다.
     *
     * @param character 삭제할 캐릭터 정보
     * @param uuid 소설 소유자 UUID
     * @throws NoPermissionException 소유자 권한이 없을 경우
     */
    public void deleteCharacter(Character character, long uuid) throws NoPermissionException {
        novelService.checkOwner(character.getNovelNum(), uuid);
        characterMapper.deleteCharacter(character.getCharacterNum());
    }

    /**
     * 소설 내 모든 캐릭터를 일괄 삭제합니다.
     * (소설 삭제 시 하위 데이터 정리에 사용)
     *
     * @param novelNum 소설 번호
     * @param uuid 소설 소유자 UUID
     * @return 삭제된 캐릭터 개수
     * @throws NoPermissionException 소설의 소유자가 아닌 경우
     */
    public int deleteCharacterList(long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return characterMapper.deleteCharacterList(novelNum);
    }

    /**
     * 캐릭터의 상세 데이터를 저장합니다.
     * (스탯, 장비, 에피소드 캐릭터 정보 포함)
     *
     * @param characterRequestDataDTO 요청 데이터
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없는 경우
     */
    @Transactional(rollbackOn = Exception.class)
    public void addCharacterData(CharacterRequestDataDTO characterRequestDataDTO, long uuid) throws NoPermissionException {
        novelService.checkOwner(characterRequestDataDTO.getNovelNum(), uuid);
        long episodeNum = characterRequestDataDTO.getEpisodeNum();
        long characterNum = characterRequestDataDTO.getCharacterNum();

        deleteEpisodeCharacter(new EpisodeCharacterDTO(episodeNum, characterNum));
        insertEpisodeCharacter(episodeNum, characterNum);
        insertCharacterStatList(episodeNum, characterNum, characterRequestDataDTO.getStats());
        insertCharacterEquipList(episodeNum, characterNum, characterRequestDataDTO.getEquipments());
    }

    /**
     * 캐릭터의 상세 데이터를 조회합니다.
     *
     * @param episodeCharacter 조회 대상 캐릭터의 에피소드 정보
     * @param uuid 요청 사용자 UUID
     * @return 캐릭터 상세 데이터 (기본 정보, 스탯, 장비, 계산된 스탯 포함)
     * @throws NoPermissionException 접근 권한이 없을 경우
     */
    public CharacterResponseDataDTO selectCharacterData(EpisodeCharacter episodeCharacter, long uuid)
            throws NoPermissionException {
        CharacterResponseDataDTO response = selectSimpleCharacterData(episodeCharacter, uuid);

        List<StatInfoDTO> finalStats = statCalculator.calculate(response.getStats(), response.getEquipment());
        response.setFinalStats(finalStats);

        return response;
    }

    public CharacterResponseDataDTO selectSimpleCharacterData(EpisodeCharacter episodeCharacter, long uuid) throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeCharacter.getEpisodeNum(), uuid);
        checkCharacterOwner(uuid, episodeCharacter.getCharacterNum());

        CharacterResponseDataDTO response = new CharacterResponseDataDTO();
        response.setCharacter(selectCharacter(episodeCharacter.getCharacterNum()));

        List<StatInfoDTO> stats = selectCharacterStatsByIds(episodeCharacter);
        response.setStats(stats);

        List<Long> equips = selectCharacterEquipsByIds(episodeCharacter);
        List<EquipmentDataDTO> equipmentData = equipmentService.selectEquipmentDataList(equips);
        response.setEquipment(equipmentData);

        return response;
    }

    public CharacterResponseDataDTO selectRecentCharacterData(EpisodeCharacter episodeCharacter, long uuid) throws NoPermissionException {
        EpisodeCharacter result = episodeCharacterMapper.selectRecentEpisodeCharacter(episodeCharacter);

        return result != null ? selectSimpleCharacterData(result, uuid) : null;
    }

    /**
     * 에피소드에 등장하는 캐릭터 목록을 조회합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param uuid 사용자 UUID
     * @return 캐릭터 목록
     * @throws NoPermissionException 에피소드 접근 권한이 없을 경우
     */
    public List<Character> selectCharactersByEpisode(long episodeNum, long uuid) throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeNum, uuid);
        return episodeCharacterMapper.selectCharactersByEpisode(episodeNum);
    }

    /**
     * 에피소드와 캐릭터의 관계를 추가합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param characterNum 캐릭터 번호
     */
    public void insertEpisodeCharacter(long episodeNum, long characterNum) {
        EpisodeCharacter dto = new EpisodeCharacter();
        dto.setEpisodeNum(episodeNum);
        dto.setCharacterNum(characterNum);
        episodeCharacterMapper.insertEpisodeCharacter(dto);
    }

    /**
     * 에피소드와 캐릭터의 관계를 삭제합니다.
     *
     * @param episodeCharacter 삭제할 관계 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void deleteEpisodeCharacter(EpisodeCharacter episodeCharacter, long uuid) throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeCharacter.getEpisodeNum(), uuid);
        checkCharacterOwner(episodeCharacter.getCharacterNum(), uuid);
        episodeCharacterMapper.deleteEpisodeCharacter(episodeCharacter);
    }

    /**
     * 권한 검증 없이 에피소드-캐릭터 관계를 삭제합니다.
     *
     * @param episodeCharacter 삭제할 관계 정보
     */
    public void deleteEpisodeCharacter(EpisodeCharacter episodeCharacter) {
        episodeCharacterMapper.deleteEpisodeCharacter(episodeCharacter);
    }

    /**
     * 캐릭터 스탯 정보를 조회합니다.
     *
     * @param episodeCharacter 조회 대상
     * @return 스탯 정보 목록
     */
    public List<StatInfoDTO> selectCharacterStatsByIds(EpisodeCharacter episodeCharacter) {
        return characterStatMapper.selectCharacterStatsResponse(episodeCharacter);
    }

    /**
     * 단일 캐릭터 스탯을 등록합니다.
     *
     * @param characterStat 등록할 스탯 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void insertCharacterStat(CharacterStat characterStat, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterStat.getCharacterNum());
        characterStatMapper.insertCharacterStat(characterStat);
    }

    /**
     * 캐릭터 스탯 목록을 일괄 등록합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param characterNum 캐릭터 번호
     * @param statInfoDTOS 등록할 스탯 정보 목록
     */
    public void insertCharacterStatList(long episodeNum, long characterNum, List<StatInfoDTO> statInfoDTOS) {
        if(statInfoDTOS == null || statInfoDTOS.isEmpty()
                || statInfoDTOS.stream().allMatch(dto -> dto.getStatName().isBlank())) {
            return;
        }
        List<String> statNames = statInfoDTOS.stream().map(StatInfoDTO::getStatName).collect(Collectors.toList());

        List<Stat> stats = statService.selectStatList(statNames);


        Map<String, Long> statCodeMap = stats.stream()
                .collect(Collectors.toMap(Stat::getStatName, Stat::getStatCode));

        List<StatRequestDTO> statRequests = statInfoDTOS.stream()
                .map(resp -> {
                    StatRequestDTO dto = new StatRequestDTO();
                    dto.setStatCode(statCodeMap.get(resp.getStatName()));
                    dto.setValue(resp.getValue());
                    return dto;
                })
                .toList();

        characterStatMapper.insertCharacterStatList(episodeNum, characterNum, statRequests);
    }

    /**
     * 캐릭터 스탯 정보를 수정합니다.
     *
     * @param characterStat 수정할 스탯 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void updateCharacterStat(CharacterStat characterStat, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterStat.getCharacterNum());
        characterStatMapper.updateCharacterStat(characterStat);
    }

    /**
     * 캐릭터 스탯을 삭제합니다.
     *
     * @param characterStat 삭제할 스탯 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void deleteCharacterStat(CharacterStat characterStat, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterStat.getCharacterNum());
        characterStatMapper.deleteCharacterStat(characterStat);
    }



    /**
     * 캐릭터의 모든 장비 목록을 조회합니다.
     *
     * @param episodeCharacter 에피소드-캐릭터 관계 정보
     * @return 장비 목록
     */
    public List<Long> selectCharacterEquipsByIds(EpisodeCharacter episodeCharacter) {
        return characterEquipMapper.selectCharacterEquipsByIds(
                episodeCharacter.getEpisodeNum(),
                episodeCharacter.getCharacterNum()
        );
    }

    /**
     * 캐릭터 장비를 등록합니다.
     *
     * @param characterEquip 등록할 장비 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void insertCharacterEquip(CharacterEquip characterEquip, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterEquip.getCharacterNum());
        characterEquipMapper.insertCharacterEquip(characterEquip);
    }

    /**
     * 캐릭터 장비를 일괄 등록합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param characterNum 캐릭터 번호
     * @param equipments 장비 번호 목록
     */
    public void insertCharacterEquipList(long episodeNum, long characterNum, List<Long> equipments) {
        if((equipments != null) && !equipments.isEmpty()) {
            characterEquipMapper.insertCharacterEquipList(episodeNum, characterNum, equipments);
        }
    }

    /**
     * 캐릭터 장비를 삭제합니다.
     *
     * @param characterEquip 삭제할 장비 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void deleteCharacterEquip(CharacterEquip characterEquip, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterEquip.getCharacterNum());
        characterEquipMapper.deleteCharacterEquip(characterEquip);
    }
}
