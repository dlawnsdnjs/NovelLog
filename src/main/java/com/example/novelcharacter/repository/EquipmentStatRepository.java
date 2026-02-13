package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatInfoDTO;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatInfoWithNumDTO;
import com.example.novelcharacter.domain.Equipment.dto.EquipmentStatRequestDTO;
import com.example.novelcharacter.domain.Equipment.entity.EquipmentStat;
import com.example.novelcharacter.domain.Equipment.entity.EquipmentStatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentStatRepository extends JpaRepository<EquipmentStat, EquipmentStatId> {
    public EquipmentStat findEquipmentStatById(EquipmentStatId id);

    @Query("select new com.example.novelcharacter.domain.Equipment.dto.EquipmentStatInfoWithNumDTO( " +
            "es.equipment.equipmentNum, s.statName, es.value, es.id.statType ) " +
            "from EquipmentStat es join es.stat s " +
            "where es.equipment.equipmentNum in :equipmentNums")
    public List<EquipmentStatInfoWithNumDTO> findByIdEquipmentNumIn(@Param("equipmentNums") List<Long> equipmentNums);

    @Query("select new com.example.novelcharacter.domain.Equipment.dto.EquipmentStatInfoDTO( " +
            "s.statName, es.value, es.id.statType) " +
            "from EquipmentStat es join es.stat s " +
            "where es.equipment.equipmentNum = :equipmentNum")
    public List<EquipmentStatInfoDTO> findByIdEquipmentNum(@Param("equipmentNum") long equipmentNum);

    public void insertEquipmentStats(long equipmentNum, List<EquipmentStatRequestDTO> stats);
}
