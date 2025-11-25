package com.example.novelcharacter.dto.Episode;

import com.example.novelcharacter.dto.Character.CharacterDTO;
import com.example.novelcharacter.dto.Equipment.EquipmentDataDTO;
import com.example.novelcharacter.dto.Stat.StatInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class CharacterResponseDataDTO {
    private CharacterDTO character;
    private List<StatInfoDTO> stats;
    private List<EquipmentDataDTO> equipment;
    private List<StatInfoDTO> finalStats;
}
