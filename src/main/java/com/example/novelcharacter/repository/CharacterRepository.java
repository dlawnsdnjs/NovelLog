package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long>
{
    @Query("select count(c) > 0 " +
            "from Character c join c.novel n " +
            "where n.user.uuid = :uuid and c.characterNum = :characterNum")
    public boolean existsByUuidAndCharacterNum(@Param("uuid") long uuid, @Param("characterNum") long characterNum);
    public Character findCharacterByCharacterNum(long characterNum);
    public List<Character> findCharactersByNovel_NovelNum(long novelNum);
    public List<Character> searchCharacterList(long novelNum, String search);
    public int deleteCharactersByNovel_NovelNum(long novelNum);

}
