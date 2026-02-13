package com.example.novelcharacter.mapper;

import com.example.novelcharacter.domain.Character.entity.Character;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CharacterMapper {
    public void insertCharacter(Character character);
    public int checkCharacterOwner(long uuid, long characterNum);
    public Character selectCharacter(long characterNum);
    public List<Character> selectCharacterList(long novelNum);
    public List<Character> searchCharacterList(long novelNum, String search);
    public void updateCharacter(Character character);
    public int deleteCharacter(long characterNum);
    public int deleteCharacterList(long novelNum);
}
