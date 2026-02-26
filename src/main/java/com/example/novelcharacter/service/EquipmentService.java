package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.Equipment.dto.*;
import com.example.novelcharacter.domain.Equipment.entity.Equipment;
import com.example.novelcharacter.domain.Stat.entity.Stat;
import com.example.novelcharacter.domain.Stat.dto.StatRequestDTO;
import com.example.novelcharacter.repository.EquipmentRepository;
import com.example.novelcharacter.repository.EquipmentStatBatchRepository;
import com.example.novelcharacter.repository.EquipmentStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 장비(Equipment) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>장비의 생성, 수정, 삭제, 조회, 스탯 관리 등 복합적인 로직을 담당하며
 * {@link EquipmentMapper}, {@link EquipmentStatMapper}, {@link StatService}, {@link NovelService}
 * 와 연동하여 동작합니다.</p>
 *
 * @author
 * @since 2025-10-15
 */
@RequiredArgsConstructor
@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentStatRepository equipmentStatRepository;
    private final StatService statService;
    private final NovelService novelService;
    private final EquipmentStatBatchRepository equipmentStatBatchRepository;


    /**
     * 장비 번호로 특정 장비 정보를 조회합니다.
     *
     * @param equipmentNum 장비 고유 번호
     * @return 장비 정보 DTO
     */
    public Equipment selectEquipmentById(long equipmentNum) {
        return equipmentRepository.findEquipmentByEquipmentNum(equipmentNum);
    }

    /**
     * 특정 장비의 소유권을 검증합니다.
     *
     * @param uuid 사용자 UUID
     * @param equipmentNum 장비 번호
     * @throws NoPermissionException 사용자가 해당 장비의 소유자가 아닐 경우
     */
    public void checkEquipmentOwner(long uuid, long equipmentNum) throws NoPermissionException {
        if (!equipmentRepository.existsByUuidAndEquipmentNum(uuid, equipmentNum)) {
            throw new NoPermissionException("해당 유저의 장비가 아닙니다.");
        }
    }

    /**
     * 장비 ID 목록을 기반으로 여러 장비를 조회합니다.
     *
     * @param equipmentNum 장비 ID 리스트
     * @return 조회된 장비 목록
     */
    public List<EquipmentDTO> selectEquipmentsByIds(List<Long> equipmentNum) {
        List<Equipment> equipmentList = equipmentRepository.findByEquipmentNumIn(equipmentNum);
        return equipmentList.stream().map(EquipmentDTO::from).collect(Collectors.toList());
    }

    /**
     * 특정 소설에 속한 모든 장비를 조회합니다.
     *
     * @param novelNum 소설 번호
     * @param uuid 사용자 UUID
     * @return 장비 목록
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    public List<EquipmentDTO> selectEquipmentsByNovel(long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        List<Equipment> equipmentList = equipmentRepository.findEquipmentsByNovel_NovelNum(novelNum);
        return equipmentList.stream().map(EquipmentDTO::from).collect(Collectors.toList());
    }

    /**
     * 특정 소설의 장비 목록을 페이징하여 조회합니다.
     *
     * @param novelNum 소설 번호
     * @param offset 시작 위치 (페이지 오프셋)
     * @param uuid 사용자 UUID
     * @return 페이징된 장비 목록
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    public List<EquipmentDTO> selectEquipmentsPageByNovel(long novelNum, int offset, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        Pageable pageable = PageRequest.of(offset-1, 20, Sort.by("novelNum").descending());
        List<Equipment> equipmentList = equipmentRepository.findByNovel_NovelNum(novelNum, pageable);
        return equipmentList.stream().map(EquipmentDTO::from).collect(Collectors.toList());
    }

    /**
     * 새로운 장비와 그에 속한 스탯들을 등록합니다.
     *
     * @param equipmentData 장비 및 스탯 정보를 담은 DTO
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    public void insertEquipment(EquipmentDataDTO equipmentData, long uuid) throws NoPermissionException {
        EquipmentDTO equipment = equipmentData.getEquipment();
        novelService.checkOwner(equipment.getNovelNum(), uuid);
        Equipment e = new Equipment();
        e.setEquipmentNum(equipment.getEquipmentNum());
        e.setEquipmentName(equipment.getEquipmentName());
        e.setInform(equipment.getInform());
        e.setNovel(novelService.getNovelProxy(equipment.getNovelNum()));

        insertEquipmentStatList(equipment.getEquipmentNum(), equipmentData.getEquipmentStats());
        equipmentRepository.save(e);
    }

    /**
     * 장비 정보를 수정합니다.
     *
     * <p>해당 장비의 스탯 목록도 함께 갱신됩니다.
     * (현재 N+1 문제 개선 필요)</p>
     *
     * @param equipmentData 수정할 장비 및 스탯 데이터
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    @Transactional
    public void updateEquipment(EquipmentDataDTO equipmentData, long uuid) throws NoPermissionException {
        // N+1 문제 있어서 수정 필요
        EquipmentDTO equipment = equipmentData.getEquipment();
        long equipmentNum = equipment.getEquipmentNum();
        novelService.checkOwner(equipment.getNovelNum(), uuid);
        Equipment e = equipmentRepository.findEquipmentByEquipmentNum(equipmentNum);
        e.setEquipmentName(equipment.getEquipmentName());

        equipmentStatRepository.deleteEquipmentStatsByEquipmentNum(equipmentNum);
        List<EquipmentStatRequestDTO> equipmentStatRequestDTOS = statExtractor(equipmentData.getEquipmentStats());
        equipmentStatBatchRepository.equipmentStatBatchInsert(equipmentNum, equipmentStatRequestDTOS);
    }

    /**
     * 장비를 삭제합니다.
     *
     * @param equipment 삭제할 장비 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    @Transactional
    public void deleteEquipment(EquipmentDTO equipment, long uuid) throws NoPermissionException {
        novelService.checkOwner(equipment.getNovelNum(), uuid);
        Equipment e = equipmentRepository.findEquipmentByEquipmentNum(equipment.getEquipmentNum());
        equipmentRepository.delete(e);
    }

    // ---------------------- 장비 스탯 관련 ----------------------

    /**
     * 여러 장비 스탯을 일괄 등록합니다.
     *
     * @param equipmentNum 장비 번호
     * @param equipmentStats 스탯 정보 리스트
     * @throws IllegalArgumentException 존재하지 않는 스탯 이름이 포함된 경우
     */
    public void insertEquipmentStatList(long equipmentNum, List<EquipmentStatInfoDTO> equipmentStats) {

        List<EquipmentStatRequestDTO> statRequests = statExtractor(equipmentStats);

        equipmentStatBatchRepository.equipmentStatBatchInsert(equipmentNum, statRequests);
    }

    public List<EquipmentStatRequestDTO> statExtractor(List<EquipmentStatInfoDTO> equipmentStats) {
        List<String> statNames = equipmentStats.stream()
                .map(EquipmentStatInfoDTO::getStatName)
                .collect(Collectors.toList());

        List<Stat> stats = statService.selectStatList(statNames);
        Map<String, Long> statCodeMap = stats.stream()
                .collect(Collectors.toMap(Stat::getStatName, Stat::getStatCode));

        return equipmentStats.stream()
                .map(equipStat -> {
                    Long statCode = statCodeMap.get(equipStat.getStatName());
                    if (statCode == null) {
                        throw new IllegalArgumentException("존재하지 않는 스탯: " + equipStat.getStatName());
                    }

                    StatRequestDTO statRequest = new StatRequestDTO();
                    statRequest.setStatCode(statCode);
                    statRequest.setValue(equipStat.getValue());

                    EquipmentStatRequestDTO dto = new EquipmentStatRequestDTO();
                    dto.setStat(statRequest);
                    dto.setType(equipStat.getType());
                    return dto;
                })
                .collect(Collectors.toList());

    }


    /**
     * 장비 전체 정보(기본 정보 + 스탯)를 조회합니다.
     *
     * @param uuid 사용자 UUID
     * @param equipmentNum 장비 번호
     * @return 장비 데이터 DTO
     * @throws NoPermissionException 사용자가 해당 장비의 소유자가 아닐 경우
     */
    public EquipmentDataDTO selectEquipmentData(long uuid, long equipmentNum) throws NoPermissionException {
        checkEquipmentOwner(uuid, equipmentNum);

        return equipmentRepository.findEquipmentDataByEquipmentNum(equipmentNum);
    }

    /**
     * 여러 장비의 상세 데이터를 한 번에 조회합니다.
     *
     * @param equipmentIds 장비 ID 목록
     * @return 장비 및 스탯 정보를 포함한 DTO 리스트
     */
    public List<EquipmentDataDTO> selectEquipmentDataList(List<Long> equipmentIds) {
        if(equipmentIds == null || equipmentIds.isEmpty()) {
            return null;
        }

        return equipmentRepository.findEquipmentDataByEquipmentNumIn(equipmentIds);
    }
}
