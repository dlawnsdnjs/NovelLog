package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Character.entity.Character;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EpisodeCharacterMapper {
    public List<EpisodeCharacter> selectEpisodeCharacterByEpisode(long episodeNum);
    public List<Character> selectCharactersByEpisode(long episodeNum);
    public EpisodeCharacter selectRecentEpisodeCharacter(EpisodeCharacter episodeCharacter);
    public void insertEpisodeCharacter(EpisodeCharacter episodeCharacter);
    public void deleteEpisodeCharacter(EpisodeCharacter episodeCharacter);
}
