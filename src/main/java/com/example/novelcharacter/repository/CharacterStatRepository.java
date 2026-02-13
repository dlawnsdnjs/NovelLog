package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Episode.entity.CharacterStat;
import com.example.novelcharacter.domain.Episode.entity.CharacterStatId;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacter;
import com.example.novelcharacter.domain.Episode.entity.EpisodeCharacterId;
import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterStatRepository extends JpaRepository<CharacterStat, CharacterStatId> {

    @Query("select new com.example.novelcharacter.domain.Stat.dto.StatInfoDTO(s.statName, cs.value) " +
            "from CharacterStat cs join cs.stat s " +
            "where cs.episodeCharacter = :episodeCharacter")
    public List<StatInfoDTO> findByEpisodeCharacter(EpisodeCharacter episodeCharacter);
    public void insertCharacterStats(long episodeNum, long characterNum, List<StatRequestDTO> stats);
}
