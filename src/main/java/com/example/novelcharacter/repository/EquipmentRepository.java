package com.example.novelcharacter.repository;

import com.example.novelcharacter.domain.Equipment.dto.EquipmentDataDTO;
import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    public Equipment findEquipmentByEquipmentNum(long equipmentNum);
    public List<Equipment> findByEquipmentNumIn(List<Long> equipmentIds);
    public List<Equipment> findEquipmentsByNovel_NovelNum(long novelNum);
    public List<Equipment> findByNovel_NovelNum(long novelNum, Pageable pageable);

    @Query("select distinct e from Equipment e " +
            "join fetch e.stats es " + // 1:N 페치 조인
            "join fetch es.stat s " +  // N:1 페치 조인
            "where e.equipmentNum in :ids")
    public EquipmentDataDTO findEquipmentDataByEquipmentNumIn(@Param("ids") List<Long> ids);

    @Query("select count(e) > 0 " +
            "from Equipment e join e.novel n " +
            "where n.user.uuid = :uuid and e.equipmentNum = :equipmentNum")
    public boolean existsByUuidAndEquipmentNum(@Param("uuid") long uuid, @Param("equipmentNum") long equipmentNum);
}
