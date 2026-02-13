package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Episode.entity.CharacterStat;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CharacterStatMapper {
    public List<CharacterStat> selectCharacterStatsByIds(long episodeNum, long characterNum);
    public List<StatInfoDTO> selectCharacterStatsResponse(EpisodeCharacter episodeCharacter);
    public void insertCharacterStat(CharacterStat characterStat);
    public void insertCharacterStatList(long episodeNum, long characterNum, List<StatRequestDTO> stats);
    public void updateCharacterStat(CharacterStat characterStat);
    public void deleteCharacterStat(CharacterStat characterStat);
}
