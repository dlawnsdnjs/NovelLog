package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Character.entity.Character;
import com.example.novelcharacter.domain.Episode.entity.Episode;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeCharacterRepository extends JpaRepository<EpisodeCharacter, EpisodeCharacterId> {
    @Query("select c " +
            "from EpisodeCharacter ec join ec.character c " +
            "where ec.episode.episodeNum = :episodeNum")
    public List<Character> findCharactersByIdEpisodeNum(@Param("episodeNum") long episodeNum);

    @Query("select ec from EpisodeCharacter ec " +
            "where ec.id.characterNum = :characterNum and ec.id.episodeNum < :episodeNum " +
            "order by ec.id.episodeNum desc limit 1")
    public EpisodeCharacter findRecentEpisodeCharacter(@Param("episodeNum") long episodeNum, @Param("characterNum") long characterNum);

    @Modifying
    @Query("delete from EpisodeCharacter ec where ec.id.episodeNum = :episodeNum and ec.id.characterNum = :characterNum")
    public void deleteByEpisodeCharacter(@Param("episodeNum") long episodeNum, @Param("characterNum") long characterNum);
}
