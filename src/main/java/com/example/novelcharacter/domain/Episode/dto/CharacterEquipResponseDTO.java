package com.example.novelcharacter.domain.Episode.dto;

import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
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
