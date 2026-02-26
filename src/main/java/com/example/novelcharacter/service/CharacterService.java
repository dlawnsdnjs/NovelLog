package com.example.novelcharacter.service;

import com.example.novelcharacter.component.StatCalculator;
import com.example.novelcharacter.domain.Character.dto.CharacterDTO;
import com.example.novelcharacter.domain.Character.entity.Character;
import com.example.novelcharacter.domain.Episode.dto.CharacterRequestDataDTO;
import com.example.novelcharacter.domain.Episode.dto.CharacterResponseDataDTO;
import com.example.novelcharacter.domain.Episode.dto.EpisodeCharacterDTO;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentDataDTO;
import com.example.novelcharacter.domain.Novel.entity.Novel;
import com.example.novelcharacter.domain.Stat.entity.Stat;
import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
import com.example.novelcharacter.repository.*;
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

    private final NovelService novelService;
    private final EpisodeService episodeService;
    private final EquipmentService equipmentService;
    private final StatService statService;
    private final StatCalculator statCalculator;
    private final CharacterRepository characterRepository;
    private final CharacterStatRepository characterStatRepository;
    private final EpisodeCharacterRepository episodeCharacterRepository;
    private final CharacterStatBatchRepository characterStatBatchRepository;
    private final CharacterEquipRepository characterEquipRepository;
    private final CharacterEquipBatchRepository characterEquipBatchRepository;


    /**
     * 새로운 캐릭터를 등록합니다.
     *
     * @param character 등록할 캐릭터 정보
     * @param uuid 소설 소유자 UUID
     * @throws NoPermissionException 소설의 소유자가 아닌 경우
     */
    @Transactional
    public void insertCharacter(CharacterDTO character, long uuid) throws NoPermissionException {
        novelService.checkOwner(character.getNovelNum(), uuid);
        Character c = new Character();
        c.setCharacterName(character.getCharacterName());
        Novel n = novelService.getNovelProxy(character.getNovelNum());
        c.setNovel(n);
        characterRepository.save(c);
    }


    public CharacterDTO selectCharacter(long characterNum) {
        return CharacterDTO.from(characterRepository.findCharacterByCharacterNum(characterNum));
    }

    public void checkCharacterOwner(long uuid, long characterNum) throws NoPermissionException {
        if (!characterRepository.existsByUuidAndCharacterNum(uuid, characterNum)) {
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
        return characterRepository.findCharactersByNovel_NovelNum(novelNum).stream().map(CharacterDTO::from).collect(Collectors.toList());
    }

    /**
     * 캐릭터 이름을 기준으로 검색합니다.
     *
     * @param novelNum 소설 번호
     * @param character 검색할 캐릭터명
     * @return 검색된 캐릭터 목록
     */
    public List<CharacterDTO> searchCharacterList(long novelNum, String character) {
        return characterRepository.searchCharacterList(novelNum, character).stream().map(CharacterDTO::from).collect(Collectors.toList());
    }

    /**
     * 캐릭터 정보를 수정합니다.
     *
     * @param character 수정할 캐릭터 정보
     */
    @Transactional
    public void updateCharacter(CharacterDTO character) {
        Character c = characterRepository.findCharacterByCharacterNum(character.getCharacterNum());
        c.setCharacterName(character.getCharacterName());
    }

    /**
     * 캐릭터를 삭제합니다.
     *
     * @param character 삭제할 캐릭터 정보
     * @param uuid 소설 소유자 UUID
     * @throws NoPermissionException 소유자 권한이 없을 경우
     */
    @Transactional
    public void deleteCharacter(CharacterDTO character, long uuid) throws NoPermissionException {
        novelService.checkOwner(character.getNovelNum(), uuid);
        Character c = characterRepository.findCharacterByCharacterNum(character.getCharacterNum());
        characterRepository.delete(c);
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
        return characterRepository.deleteCharactersByNovel_NovelNum(novelNum);
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

        deleteEpisodeCharacter(episodeNum, characterNum);
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
    public CharacterResponseDataDTO selectCharacterData(EpisodeCharacterDTO episodeCharacter, long uuid)
            throws NoPermissionException {
        CharacterResponseDataDTO response = selectSimpleCharacterData(episodeCharacter, uuid);

        List<StatInfoDTO> finalStats = statCalculator.calculate(response.getStats(), response.getEquipment());
        response.setFinalStats(finalStats);

        return response;
    }

    public CharacterResponseDataDTO selectSimpleCharacterData(EpisodeCharacterDTO episodeCharacter, long uuid) throws NoPermissionException {
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

    public CharacterResponseDataDTO selectRecentCharacterData(EpisodeCharacterDTO episodeCharacter, long uuid) throws NoPermissionException {
        EpisodeCharacter result = episodeCharacterRepository.findRecentEpisodeCharacter(episodeCharacter.getEpisodeNum(), episodeCharacter.getCharacterNum());

        return result != null ? selectSimpleCharacterData(EpisodeCharacterDTO.from(result), uuid) : null;
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
        return episodeCharacterRepository.findCharactersByIdEpisodeNum(episodeNum).stream().map(CharacterDTO::from).collect(Collectors.toList());
    }

    /**
     * 에피소드와 캐릭터의 관계를 추가합니다.
     *
     * @param episodeNum 에피소드 번호
     * @param characterNum 캐릭터 번호
     */
    public void insertEpisodeCharacter(long episodeNum, long characterNum) {
        EpisodeCharacter dto = new EpisodeCharacter();
        dto.setEpisode(episodeService.getEpisodeProxy(episodeNum));
        dto.setCharacter(characterRepository.getReferenceById(characterNum));
        episodeCharacterRepository.save(dto);
    }

    /**
     * 에피소드와 캐릭터의 관계를 삭제합니다.
     *
     * @param episodeCharacter 삭제할 관계 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 권한이 없을 경우
     */
    @Transactional
    public void deleteEpisodeCharacter(EpisodeCharacterDTO episodeCharacter, long uuid) throws NoPermissionException {
        episodeService.checkEpisodeOwner(episodeCharacter.getEpisodeNum(), uuid);
        checkCharacterOwner(episodeCharacter.getCharacterNum(), uuid);

        episodeCharacterRepository.deleteByEpisodeCharacter(episodeCharacter.getEpisodeNum(), episodeCharacter.getCharacterNum());
    }

    @Transactional
    public void deleteEpisodeCharacter(long episodeNum, long characterNum){
        episodeCharacterRepository.deleteByEpisodeCharacter(episodeNum, characterNum);
    }

    /**
     * 캐릭터 스탯 정보를 조회합니다.
     *
     * @param episodeCharacter 조회 대상
     * @return 스탯 정보 목록
     */
    public List<StatInfoDTO> selectCharacterStatsByIds(EpisodeCharacterDTO episodeCharacter) {
        return characterStatRepository.findByEpisodeCharacter(episodeCharacter);
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

        characterStatBatchRepository.characterStatBatchInsert(episodeNum, characterNum, statRequests);
    }



    /**
     * 캐릭터의 모든 장비 목록을 조회합니다.
     *
     * @param episodeCharacter 에피소드-캐릭터 관계 정보
     * @return 장비 목록
     */
    public List<Long> selectCharacterEquipsByIds(EpisodeCharacterDTO episodeCharacter) {
        return characterEquipRepository.findCharacterEquipsByEpisodeCharacter(episodeCharacter);
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
            characterEquipBatchRepository.characterEquipBatchInsert(episodeNum, characterNum, equipments);
        }
    }


}
