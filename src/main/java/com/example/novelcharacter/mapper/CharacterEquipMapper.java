package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Episode.CharacterEquipDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CharacterEquipMapper {
    public CharacterEquipDTO selectCharacterEquipByIds(long episodeNum, long characterNum, long equipmentNum); // 필요 없어 보임 그냥 Equipment에서 장비 정보 받아오는게 맞음
    public List<Long> selectCharacterEquipsByIds(long episodeNum, long characterNum); // 아마 이걸로 장비번호를 받아서 Equipment에서 받아오는 식으로 작동
    public void insertCharacterEquip(CharacterEquipDTO characterEquipDTO);
    public void insertCharacterEquipList(long episodeNum, long characterNum, List<Long> equipments);
    public void deleteCharacterEquip(CharacterEquipDTO characterEquipDTO);
}
