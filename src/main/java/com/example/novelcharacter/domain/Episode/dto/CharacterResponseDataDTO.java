package com.example.novelcharacter.domain.Episode.dto;

import com.example.novelcharacter.domain.Character.dto.CharacterDTO;
import com.example.novelcharacter.domain.Character.entity.Character;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentDataDTO;
import com.example.novelcharacter.domain.Stat.dto.StatInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class CharacterResponseDataDTO {
    private CharacterDTO character;
    private List<StatInfoDTO> stats;
    private List<EquipmentDataDTO> equipment;
    private List<StatInfoDTO> finalStats;
}
