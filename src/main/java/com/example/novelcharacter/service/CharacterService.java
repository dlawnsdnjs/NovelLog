package com.example.novelcharacter.service;

import com.example.novelcharacter.component.StatCalculator;
import com.example.novelcharacter.dto.*;
import com.example.novelcharacter.mapper.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
     * CharacterService 생성자.
     *
     * @param characterMapper 캐릭터 매퍼
     * @param episodeCharacterMapper 에피소드-캐릭터 매퍼
     * @param characterStatMapper 캐릭터 스탯 매퍼
     * @param characterEquipMapper 캐릭터 장비 매퍼
     * @param novelService 소설 서비스
     * @param episodeService 에피소드 서비스
     * @param equipmentService 장비 서비스
     * @param statService 스탯 서비스
     * @param statCalculator 스탯 계산기
     */
    @Autowired
    public CharacterService(CharacterMapper characterMapper,
                            EpisodeCharacterMapper episodeCharacterMapper,
                            CharacterStatMapper characterStatMapper,
                            CharacterEquipMapper characterEquipMapper,
                            NovelService novelService,
                            EpisodeService episodeService,
                            EquipmentService equipmentService,
                            StatService statService,
                            StatCalculator statCalculator) {
        this.characterMapper = characterMapper;
        this.episodeCharacterMapper = episodeCharacterMapper;
        this.characterStatMapper = characterStatMapper;
        this.characterEquipMapper = characterEquipMapper;
        this.novelService = novelService;
        this.episodeService = episodeService;
        this.equipmentService = equipmentService;
        this.statService = statService;
        this.statCalculator = statCalculator;
    }

    /**
     * 새로운 캐릭터를 등록합니다.
     *
     * @param characterDTO 등록할 캐릭터 정보
     * @param uuid 소설 소유자 UUID
     * @throws NoPermissionException 소설의 소유자가 아닌 경우
     */
    public void insertCharacter(CharacterDTO characterDTO, long uuid) throws NoPermissionException {
        novelService.checkOwner(characterDTO.getNovelNum(), uuid);
        characterMapper.insertCharacter(characterDTO);
    }

    /**
     * 캐릭터 번호로 캐릭터 정보를 조회합니다.
     *
     * @param characterNum 캐릭터 번호
     * @return 캐릭터 정보
     */
    public CharacterDTO selectCharacter(long characterNum) {
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
    public List<CharacterDTO> selectCharacterList(long novelNum, long uuid) throws NoPermissionException {
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
    public List<CharacterDTO> searchCharacterList(long novelNum, String character) {
        return characterMapper.searchCharacterList(novelNum, character);
    }

    /**
     * 캐릭터 정보를 수정합니다.
     *
     * @param characterDTO 수정할 캐릭터 정보
     */
    public void updateCharacter(CharacterDTO characterDTO) {
        characterMapper.updateCharacter(characterDTO);
    }

    /**
     * 캐릭터를 삭제합니다.
     *
     * @param characterDTO 삭제할 캐릭터 정보
     * @param uuid 소설 소유자 UUID
     * @throws NoPermissionException 소유자 권한이 없을 경우
     */
    public void deleteCharacter(CharacterDTO characterDTO, long uuid) throws NoPermissionException {
        novelService.checkOwner(characterDTO.getNovelNum(), uuid);
        characterMapper.deleteCharacter(characterDTO.getCharacterNum());
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
     * @param episodeCharacterDTO 조회 대상 캐릭터의 에피소드 정보
     * @param uuid 요청 사용자 UUID
     * @return 캐릭터 상세 데이터 (기본 정보, 스탯, 장비, 계산된 스탯 포함)
     * @throws NoPermissionException 접근 권한이 없을 경우
     */
    public CharacterResponseDataDTO selectCharacterData(EpisodeCharacterDTO episodeCharacterDTO, long uuid)
            throws NoPermissionException {
        CharacterResponseDataDTO response = selectSimpleCharacterData(episodeCharacterDTO, uuid);

        List<StatInfoDTO> finalStats = statCalculator.calculate(response.getStats(), response.getEquipment());
        response.setFinalStats(finalStats);

        return response;
    }

    public CharacterResponseDataDTO selectSimpleCharacterData(EpisodeCharacterDTO episodeCharacterDTO, long uuid) throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeCharacterDTO.getEpisodeNum(), uuid);
        checkCharacterOwner(uuid, episodeCharacterDTO.getCharacterNum());

        CharacterResponseDataDTO response = new CharacterResponseDataDTO();
        response.setCharacter(selectCharacter(episodeCharacterDTO.getCharacterNum()));

        List<StatInfoDTO> stats = selectCharacterStatsByIds(episodeCharacterDTO);
        response.setStats(stats);

        List<CharacterEquipDTO> equips = selectCharacterEquipsByIds(episodeCharacterDTO);
        List<Long> equipList = equips.stream().map(CharacterEquipDTO::getEquipmentNum).toList();
        List<EquipmentDataDTO> equipmentData = equipmentService.selectEquipmentDataList(equipList);
        response.setEquipment(equipmentData);

        return response;
    }

    public CharacterResponseDataDTO selectRecentCharacterData(EpisodeCharacterDTO episodeCharacterDTO, long uuid) throws NoPermissionException {
        EpisodeCharacterDTO result = episodeCharacterMapper.selectRecentEpisodeCharacter(episodeCharacterDTO);
        return selectSimpleCharacterData(result, uuid);
    }

    /**
     * 에피소드에 등장하는 캐릭터 목록을 조회합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param uuid 사용자 UUID
     * @return 캐릭터 목록
     * @throws NoPermissionException 에피소드 접근 권한이 없을 경우
     */
    public List<CharacterDTO> selectCharactersByEpisode(long episodeNum, long uuid) throws NoPermissionException {
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
        EpisodeCharacterDTO dto = new EpisodeCharacterDTO();
        dto.setEpisodeNum(episodeNum);
        dto.setCharacterNum(characterNum);
        episodeCharacterMapper.insertEpisodeCharacter(dto);
    }

    /**
     * 에피소드와 캐릭터의 관계를 삭제합니다.
     *
     * @param episodeCharacterDTO 삭제할 관계 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void deleteEpisodeCharacter(EpisodeCharacterDTO episodeCharacterDTO, long uuid) throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeCharacterDTO.getEpisodeNum(), uuid);
        checkCharacterOwner(episodeCharacterDTO.getCharacterNum(), uuid);
        episodeCharacterMapper.deleteEpisodeCharacter(episodeCharacterDTO);
    }

    /**
     * 권한 검증 없이 에피소드-캐릭터 관계를 삭제합니다.
     *
     * @param episodeCharacterDTO 삭제할 관계 정보
     */
    public void deleteEpisodeCharacter(EpisodeCharacterDTO episodeCharacterDTO) {
        episodeCharacterMapper.deleteEpisodeCharacter(episodeCharacterDTO);
    }

    /**
     * 캐릭터 스탯 정보를 조회합니다.
     *
     * @param episodeCharacterDTO 조회 대상
     * @return 스탯 정보 목록
     */
    public List<StatInfoDTO> selectCharacterStatsByIds(EpisodeCharacterDTO episodeCharacterDTO) {
        return characterStatMapper.selectCharacterStatsResponse(episodeCharacterDTO);
    }

    /**
     * 단일 캐릭터 스탯을 등록합니다.
     *
     * @param characterStatDTO 등록할 스탯 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void insertCharacterStat(CharacterStatDTO characterStatDTO, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterStatDTO.getCharacterNum());
        characterStatMapper.insertCharacterStat(characterStatDTO);
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

        List<StatDTO> statDTOS = statService.selectStatList(statNames);


        Map<String, Long> statCodeMap = statDTOS.stream()
                .collect(Collectors.toMap(StatDTO::getStatName, StatDTO::getStatCode));

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
     * @param characterStatDTO 수정할 스탯 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void updateCharacterStat(CharacterStatDTO characterStatDTO, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterStatDTO.getCharacterNum());
        characterStatMapper.updateCharacterStat(characterStatDTO);
    }

    /**
     * 캐릭터 스탯을 삭제합니다.
     *
     * @param characterStatDTO 삭제할 스탯 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void deleteCharacterStat(CharacterStatDTO characterStatDTO, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterStatDTO.getCharacterNum());
        characterStatMapper.deleteCharacterStat(characterStatDTO);
    }

    /**
     * 특정 캐릭터의 장비 정보를 조회합니다.
     *
     * @param episodeCharacterDTO 에피소드-캐릭터 관계 정보
     * @param equipmentNum 장비 번호
     * @param uuid 사용자 UUID
     * @return 캐릭터 장비 정보
     * @throws NoPermissionException 권한이 없을 경우
     */
    public CharacterEquipDTO selectCharacterEquipByIds(EpisodeCharacterDTO episodeCharacterDTO, long equipmentNum, long uuid)
            throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeCharacterDTO.getEpisodeNum(), uuid);
        checkCharacterOwner(episodeCharacterDTO.getCharacterNum(), uuid);
        return characterEquipMapper.selectCharacterEquipByIds(
                episodeCharacterDTO.getEpisodeNum(),
                episodeCharacterDTO.getCharacterNum(),
                equipmentNum
        );
    }

    /**
     * 캐릭터의 모든 장비 목록을 조회합니다.
     *
     * @param episodeCharacterDTO 에피소드-캐릭터 관계 정보
     * @return 장비 목록
     */
    public List<CharacterEquipDTO> selectCharacterEquipsByIds(EpisodeCharacterDTO episodeCharacterDTO) {
        return characterEquipMapper.selectCharacterEquipsByIds(
                episodeCharacterDTO.getEpisodeNum(),
                episodeCharacterDTO.getCharacterNum()
        );
    }

    /**
     * 캐릭터 장비를 등록합니다.
     *
     * @param characterEquipDTO 등록할 장비 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void insertCharacterEquip(CharacterEquipDTO characterEquipDTO, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterEquipDTO.getCharacterNum());
        characterEquipMapper.insertCharacterEquip(characterEquipDTO);
    }

    /**
     * 캐릭터 장비를 일괄 등록합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param characterNum 캐릭터 번호
     * @param equipments 장비 번호 목록
     */
    public void insertCharacterEquipList(long episodeNum, long characterNum, List<Long> equipments) {
        if(!equipments.isEmpty()) {
            characterEquipMapper.insertCharacterEquipList(episodeNum, characterNum, equipments);
        }
    }

    /**
     * 캐릭터 장비를 삭제합니다.
     *
     * @param characterEquipDTO 삭제할 장비 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    public void deleteCharacterEquip(CharacterEquipDTO characterEquipDTO, long uuid) throws NoPermissionException {
        checkCharacterOwner(uuid, characterEquipDTO.getCharacterNum());
        characterEquipMapper.deleteCharacterEquip(characterEquipDTO);
    }
}
