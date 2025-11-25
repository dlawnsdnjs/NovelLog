package com.example.novelcharacter.component;

import com.example.novelcharacter.dto.Equipment.EquipmentDataDTO;
import com.example.novelcharacter.dto.Equipment.EquipmentStatInfoDTO;
import com.example.novelcharacter.dto.Stat.StatInfoDTO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatCalculator {

    public List<StatInfoDTO> calculate(
            List<StatInfoDTO> baseStats,
            List<EquipmentDataDTO> equipmentList
    ) {

        // baseStats null 혹은 "의미 없는 빈 데이터" 제거
        if (baseStats == null) baseStats = List.of();
        baseStats = baseStats.stream()
                .filter(s -> s.getStatName() != null && !s.getStatName().isBlank())
                .toList();

        // equipmentList null 방지
        if (equipmentList == null) equipmentList = List.of();

        Map<String, Long> finalStats = new LinkedHashMap<>();

        // base 등록
        for (StatInfoDTO stat : baseStats) {
            finalStats.put(stat.getStatName(), stat.getValue());
        }

        // 장비 보정 적용
        for (EquipmentDataDTO equip : equipmentList) {
            if (equip == null || equip.getEquipmentStats() == null) continue; // NPE 방지

            for (EquipmentStatInfoDTO equipStat : equip.getEquipmentStats()) {
                if (equipStat.getStatName() == null || equipStat.getStatName().isBlank()) continue;

                String name = equipStat.getStatName();
                long value = equipStat.getValue();

                finalStats.putIfAbsent(name, 0L);

                if (equipStat.getType() == 0) {
                    finalStats.put(name, finalStats.get(name) + value);
                } else if (equipStat.getType() == 1) {
                    finalStats.put(name, finalStats.get(name) * value);
                }
            }
        }

        return finalStats.entrySet().stream()
                .map(e -> new StatInfoDTO(e.getKey(), e.getValue()))
                .toList();
    }
}