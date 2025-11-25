package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Character.CharacterDTO;
import com.example.novelcharacter.dto.Episode.EpisodeCharacterDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EpisodeCharacterMapper {
    public List<EpisodeCharacterDTO> selectEpisodeCharacterByEpisode(long episodeNum);
    public List<CharacterDTO> selectCharactersByEpisode(long episodeNum);
    public EpisodeCharacterDTO selectRecentEpisodeCharacter(EpisodeCharacterDTO episodeCharacterDTO);
    public void insertEpisodeCharacter(EpisodeCharacterDTO episodeCharacterDTO);
    public void deleteEpisodeCharacter(EpisodeCharacterDTO episodeCharacterDTO);
}
