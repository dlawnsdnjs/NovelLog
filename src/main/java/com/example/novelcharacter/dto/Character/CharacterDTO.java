package com.example.novelcharacter.dto.Character;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CharacterDTO {
    private long novelNum;
    @NotBlank
    @Size(max=40)
    private String characterName;
    private long characterNum;
}
