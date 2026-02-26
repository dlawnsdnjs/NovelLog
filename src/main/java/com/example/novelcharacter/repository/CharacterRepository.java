package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("select c from Character c where c.novel.novelNum = :novelNum and c.characterName like concat('%', :search, '%') ")
    public List<Character> searchCharacterList(@Param("novelNum") long novelNum, @Param("search") String search);

    @Modifying
    @Query("delete from Character c where c.novel.novelNum = :novelNum")
    public int deleteCharactersByNovel_NovelNum(@Param("novelNum") long novelNum);

}
