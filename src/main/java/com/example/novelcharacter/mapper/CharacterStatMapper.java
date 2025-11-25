package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Episode.CharacterStatDTO;
import com.example.novelcharacter.dto.Episode.EpisodeCharacterDTO;
import com.example.novelcharacter.dto.Stat.StatInfoDTO;
import com.example.novelcharacter.dto.Stat.StatRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CharacterStatMapper {
    public List<CharacterStatDTO> selectCharacterStatsByIds(long episodeNum, long characterNum);
    public List<StatInfoDTO> selectCharacterStatsResponse(EpisodeCharacterDTO episodeCharacterDTO);
    public void insertCharacterStat(CharacterStatDTO characterStatDTO);
    public void insertCharacterStatList(long episodeNum, long characterNum, List<StatRequestDTO> stats);
    public void updateCharacterStat(CharacterStatDTO characterStatDTO);
    public void deleteCharacterStat(CharacterStatDTO characterStatDTO);
}
