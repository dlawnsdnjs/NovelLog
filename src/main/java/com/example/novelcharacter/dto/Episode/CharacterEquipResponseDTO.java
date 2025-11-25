package com.example.novelcharacter.dto.Episode;

import com.example.novelcharacter.dto.Stat.StatInfoDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CharacterEquipResponseDTO {
    private String equipmentName;
    private List<StatInfoDTO> stats;
}
