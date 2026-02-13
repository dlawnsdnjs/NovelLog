package com.example.novelcharacter.domain.Character.dto;

import com.example.novelcharacter.domain.Character.entity.Character;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CharacterDTO {
    private long characterNum;
    private long novelNum;
    private String characterName;

    public static CharacterDTO from(Character character) {
        return CharacterDTO.builder()
                .characterNum(character.getCharacterNum())
                .novelNum(character.getNovel().getNovelNum())
                .characterName(character.getCharacterName())
                .build();
    }
}
