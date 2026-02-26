package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Episode.dto.EpisodeCharacterDTO;
import com.example.novelcharacter.domain.Episode.entity.CharacterEquip;
import com.example.novelcharacter.domain.Episode.entity.CharacterEquipId;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterEquipRepository extends JpaRepository<CharacterEquip, CharacterEquipId> {
    @Query("select ce.equipment.equipmentNum " +
            "from CharacterEquip ce " +
            "where ce.episodeCharacter.id.episodeNum = :#{#episodeCharacter.episodeNum} " +
            "and ce.episodeCharacter.id.characterNum = :#{#episodeCharacter.characterNum}")
    public List<Long> findCharacterEquipsByEpisodeCharacter(@Param("episodeCharacter") EpisodeCharacterDTO episodeCharacter);
}
