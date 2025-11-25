package com.example.novelcharacter.mapper;

import com.example.novelcharacter.dto.Character.CharacterDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CharacterMapper {
    public void insertCharacter(CharacterDTO characterDTO);
    public int checkCharacterOwner(long uuid, long characterNum);
    public CharacterDTO selectCharacter(long characterNum);
    public List<CharacterDTO> selectCharacterList(long novelNum);
    public List<CharacterDTO> searchCharacterList(long novelNum, String search);
    public void updateCharacter(CharacterDTO characterDTO);
    public int deleteCharacter(long characterNum);
    public int deleteCharacterList(long novelNum);
}
