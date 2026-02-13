package com.example.novelcharacter.repository;

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
    public CharacterEquip findCharacterEquipById(CharacterEquipId id);

    @Query("select ce.equipment.equipmentNum " +
            "from CharacterEquip ce " +
            "where ce.episodeCharacter = :episodeCharacter")
    public List<Long> findCharacterEquipsByEpisodeCharacter(@Param("episodeCharacter") EpisodeCharacter episodeCharacter);
    public void insertCharacterEquips(long episodeNum, long characterNum, List<Long> equipmentNums);
}
