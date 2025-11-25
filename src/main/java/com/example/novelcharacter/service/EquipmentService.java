package com.example.novelcharacter.service;

import com.example.novelcharacter.dto.Equipment.*;
import com.example.novelcharacter.dto.Stat.StatDTO;
import com.example.novelcharacter.dto.Stat.StatRequestDTO;
import com.example.novelcharacter.mapper.EquipmentMapper;
import com.example.novelcharacter.mapper.EquipmentStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.ArrayList;
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
@Service
public class EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentStatMapper equipmentStatMapper;
    private final StatService statService;
    private final NovelService novelService;

    /**
     * EquipmentService 생성자.
     *
     * @param equipmentMapper 장비 데이터 접근 매퍼
     * @param equipmentStatMapper 장비 스탯 데이터 접근 매퍼
     * @param statService 스탯 관련 서비스
     * @param novelService 소설 소유자 검증 서비스
     */
    @Autowired
    public EquipmentService(EquipmentMapper equipmentMapper, EquipmentStatMapper equipmentStatMapper,
                            StatService statService, NovelService novelService) {
        this.equipmentMapper = equipmentMapper;
        this.equipmentStatMapper = equipmentStatMapper;
        this.statService = statService;
        this.novelService = novelService;
    }

    /**
     * 장비 번호로 특정 장비 정보를 조회합니다.
     *
     * @param equipmentNum 장비 고유 번호
     * @return 장비 정보 DTO
     */
    public EquipmentDTO selectEquipmentById(long equipmentNum) {
        return equipmentMapper.selectEquipmentById(equipmentNum);
    }

    /**
     * 특정 장비의 소유권을 검증합니다.
     *
     * @param uuid 사용자 UUID
     * @param equipmentNum 장비 번호
     * @throws NoPermissionException 사용자가 해당 장비의 소유자가 아닐 경우
     */
    public void checkEquipmentOwner(long uuid, long equipmentNum) throws NoPermissionException {
        if (equipmentMapper.checkEquipmentOwner(uuid, equipmentNum) != 1) {
            throw new NoPermissionException("해당 유저의 장비가 아닙니다.");
        }
    }

    /**
     * 장비 ID 목록을 기반으로 여러 장비를 조회합니다.
     *
     * @param equipmentIds 장비 ID 리스트
     * @return 조회된 장비 목록
     */
    public List<EquipmentDTO> selectEquipmentsByIds(List<Long> equipmentIds) {
        return equipmentMapper.selectEquipmentsByIds(equipmentIds);
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
        return equipmentMapper.selectEquipmentsById(novelNum);
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
        return equipmentMapper.selectEquipmentsPageById(novelNum, offset);
    }

    /**
     * 장비 이름으로 특정 장비를 조회합니다.
     *
     * @param equipmentName 장비 이름
     * @param novelNum 소설 번호
     * @param uuid 사용자 UUID
     * @return 장비 DTO
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    public EquipmentDTO selectEquipmentByName(String equipmentName, long novelNum, long uuid) throws NoPermissionException {
        novelService.checkOwner(novelNum, uuid);
        return equipmentMapper.selectEquipmentByName(equipmentName, novelNum);
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
        equipmentMapper.insertEquipment(equipment);

        insertEquipmentStatList(equipment.getEquipmentNum(), equipmentData.getEquipmentStats());
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
    public void updateEquipment(EquipmentDataDTO equipmentData, long uuid) throws NoPermissionException {
        EquipmentDTO equipment = equipmentData.getEquipment();
        novelService.checkOwner(equipment.getNovelNum(), uuid);
        equipmentMapper.updateEquipment(equipment);

        for (EquipmentStatInfoDTO equipmentStatDTO : equipmentData.getEquipmentStats()) {
            EquipmentStatDTO equipmentStat = new EquipmentStatDTO();
            equipmentStat.setEquipmentNum(equipment.getEquipmentNum());
            StatDTO statDTO = statService.selectStat(equipmentStatDTO.getStatName());
            equipmentStat.setStatCode(statDTO.getStatCode());
            equipmentStat.setStatType(equipmentStatDTO.getType());
            equipmentStat.setValue(equipmentStatDTO.getValue());

            updateEquipmentStat(equipmentStat);
        }
    }

    /**
     * 장비를 삭제합니다.
     *
     * @param equipmentDTO 삭제할 장비 정보
     * @param uuid 사용자 UUID
     * @throws NoPermissionException 사용자가 소설의 소유자가 아닐 경우
     */
    public void deleteEquipment(EquipmentDTO equipmentDTO, long uuid) throws NoPermissionException {
        novelService.checkOwner(equipmentDTO.getNovelNum(), uuid);
        equipmentMapper.deleteEquipment(equipmentDTO.getEquipmentNum());
    }

    // ---------------------- 장비 스탯 관련 ----------------------

    /**
     * 특정 장비의 모든 스탯을 조회합니다.
     *
     * @param equipmentNum 장비 번호
     * @return 스탯 정보 리스트
     */
    public List<EquipmentStatInfoDTO> selectEquipmentStats(long equipmentNum) {
        return equipmentStatMapper.selectEquipmentStatsById(equipmentNum);
    }

    /**
     * 여러 장비의 스탯 정보를 조회합니다.
     *
     * @param equipmentIds 장비 번호 리스트
     * @return 장비별 스탯 리스트
     */
    public List<EquipmentStatInfoWithNumDTO> selectEquipmentStatsWithNum(List<Long> equipmentIds) {
        return equipmentStatMapper.selectEquipmentStatsByIds(equipmentIds);
    }

    /**
     * 단일 장비 스탯을 등록합니다.
     *
     * @param equipmentStatDTO 등록할 장비 스탯 정보
     */
    public void insertEquipmentStat(EquipmentStatDTO equipmentStatDTO) {
        equipmentStatMapper.insertEquipmentStat(equipmentStatDTO);
    }

    /**
     * 여러 장비 스탯을 일괄 등록합니다.
     *
     * @param equipmentNum 장비 번호
     * @param equipmentStats 스탯 정보 리스트
     * @throws IllegalArgumentException 존재하지 않는 스탯 이름이 포함된 경우
     */
    public void insertEquipmentStatList(long equipmentNum, List<EquipmentStatInfoDTO> equipmentStats) {
        List<String> statNames = equipmentStats.stream()
                .map(EquipmentStatInfoDTO::getStatName)
                .collect(Collectors.toList());

        List<StatDTO> statDTOS = statService.selectStatList(statNames);
        Map<String, Long> statCodeMap = statDTOS.stream()
                .collect(Collectors.toMap(StatDTO::getStatName, StatDTO::getStatCode));

        List<EquipmentStatRequestDTO> statRequests = equipmentStats.stream()
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

        equipmentStatMapper.insertEquipmentStatList(equipmentNum, statRequests);
    }

    /**
     * 장비 스탯을 삭제합니다.
     *
     * @param equipmentStatDTO 삭제할 스탯 정보
     */
    public void deleteEquipmentStat(EquipmentStatDTO equipmentStatDTO) {
        equipmentStatMapper.deleteEquipmentStat(equipmentStatDTO);
    }

    /**
     * 장비 스탯을 수정합니다.
     *
     * @param equipmentStatDTO 수정할 스탯 정보
     */
    public void updateEquipmentStat(EquipmentStatDTO equipmentStatDTO) {
        equipmentStatMapper.updateEquipmentStat(equipmentStatDTO);
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

        EquipmentDataDTO equipmentDataDTO = new EquipmentDataDTO();
        equipmentDataDTO.setEquipment(selectEquipmentById(equipmentNum));
        equipmentDataDTO.setEquipmentStats(selectEquipmentStats(equipmentNum));

        return equipmentDataDTO;
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
        List<EquipmentDTO> equipmentDTOS = selectEquipmentsByIds(equipmentIds);
        List<EquipmentStatInfoWithNumDTO> equipmentStatInfoWithNumDTOS = selectEquipmentStatsWithNum(equipmentIds);

        Map<Long, List<EquipmentStatInfoWithNumDTO>> statMap =
                equipmentStatInfoWithNumDTOS.stream()
                        .collect(Collectors.groupingBy(EquipmentStatInfoWithNumDTO::getEquipmentNum));

        List<EquipmentDataDTO> equipmentDataDTOS = new ArrayList<>();

        for (EquipmentDTO equipmentDTO : equipmentDTOS) {
            EquipmentDataDTO equipmentDataDTO = new EquipmentDataDTO();
            equipmentDataDTO.setEquipment(equipmentDTO);

            List<EquipmentStatInfoWithNumDTO> statWithNumList =
                    statMap.getOrDefault(equipmentDTO.getEquipmentNum(), new ArrayList<>());

            List<EquipmentStatInfoDTO> equipmentStats = statWithNumList.stream()
                    .map(statWithNum -> {
                        EquipmentStatInfoDTO statInfo = new EquipmentStatInfoDTO();
                        statInfo.setStatName(statWithNum.getStatName());
                        statInfo.setValue(statWithNum.getValue());
                        statInfo.setType(statWithNum.getType());
                        return statInfo;
                    })
                    .collect(Collectors.toList());

            equipmentDataDTO.setEquipmentStats(equipmentStats);
            equipmentDataDTOS.add(equipmentDataDTO);
        }

        return equipmentDataDTOS;
    }
}
