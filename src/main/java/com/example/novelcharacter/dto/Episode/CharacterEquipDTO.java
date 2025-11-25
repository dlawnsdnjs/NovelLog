package com.example.novelcharacter.dto.Episode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CharacterEquipDTO {
    private long episodeNum;
    private long characterNum;
    private long equipmentNum;
}
