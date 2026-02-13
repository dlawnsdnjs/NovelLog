package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Episode.entity.CharacterEquip;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CharacterEquipMapper {
    public List<Long> selectCharacterEquipsByIds(long episodeNum, long characterNum); // 아마 이걸로 장비번호를 받아서 Equipment에서 받아오는 식으로 작동
    public void insertCharacterEquip(CharacterEquip characterEquip);
    public void insertCharacterEquipList(long episodeNum, long characterNum, List<Long> equipments);
    public void deleteCharacterEquip(CharacterEquip characterEquip);
}
