package com.example.novelcharacter.dto.Episode;

import com.example.novelcharacter.dto.Stat.StatInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class CharacterRequestDataDTO {
    private long novelNum; // 필요없는 것 같기도 함, 추후 확인해 볼 것
    private long episodeNum;
    private long characterNum;

    private List<Long> equipments;
    private List<StatInfoDTO> stats;

}
