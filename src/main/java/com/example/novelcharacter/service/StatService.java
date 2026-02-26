package com.example.novelcharacter.service;

import com.example.novelcharacter.domain.Stat.entity.Stat;
import com.example.novelcharacter.repository.StatBatchRepository;
import com.example.novelcharacter.repository.StatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p><b>StatService</b>는 캐릭터나 장비 등의 스탯(Stat) 정보를
 * 데이터베이스에서 관리하는 서비스 계층 클래스입니다.</p>
 *
 * <p>스탯명 기준으로 존재하지 않는 항목을 자동으로 생성하고,
 * 일괄 등록 및 조회, 수정, 삭제 기능을 제공합니다.</p>
 *
 * <h2>주요 기능</h2>
 * <ul>
 *     <li>단일 스탯 등록 및 다중 스탯 일괄 등록</li>
 *     <li>스탯 코드 또는 이름을 통한 조회</li>
 *     <li>존재하지 않는 스탯 자동 생성</li>
 *     <li>스탯 정보 수정 및 삭제</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
@RequiredArgsConstructor
@Service
public class StatService {

    private final StatRepository statRepository;
    private final StatBatchRepository statBatchRepository;

    /**
     * <p>단일 스탯 정보를 데이터베이스에 등록합니다.</p>
     *
     * @param stat 등록할 스탯 정보 DTO
     */
    public void insertStat(Stat stat) {
        statRepository.save(stat);
    }

    /**
     * <p>스탯 이름 리스트를 기반으로 일괄 등록을 수행합니다.</p>
     * <p>빈 문자열이나 null 항목은 자동으로 필터링됩니다.</p>
     *
     * @param statNameList 등록할 스탯 이름 목록
     * @return 데이터베이스에 삽입된 {@link Stat} 객체 리스트
     */
    public List<Stat> insertStatList(List<String> statNameList) {
        List<Stat> statList = statNameList.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(name -> {
                    Stat dto = new Stat();
                    dto.setStatName(name);
                    return dto;
                })
                .collect(Collectors.toList());

        statBatchRepository.statBatchInsert(statList);
        return statRepository.findStatsByStatNameIn(statNameList);
    }

    /**
     * 스탯 코드를 기반으로 단일 스탯 정보를 조회합니다.
     *
     * @param statCode 조회할 스탯의 고유 코드
     * @return 해당 코드의 {@link Stat} 객체 (없으면 null)
     */
    public Stat selectStat(long statCode) {
        return statRepository.findStatByStatCode(statCode);
    }

    /**
     * <p>스탯 이름으로 스탯 정보를 조회합니다.</p>
     * <p>해당 스탯이 존재하지 않으면 자동으로 새 스탯을 등록합니다.</p>
     *
     * @param statName 조회할 스탯 이름
     * @return 조회되었거나 새로 생성된 {@link Stat} 객체
     */
    public Stat selectStat(String statName) {
        Stat stat = statRepository.findStatByStatName(statName);
        if (stat == null) {
            stat = new Stat();
            stat.setStatName(statName);
            insertStat(stat);
        }
        return stat;
    }

    /**
     * <p>스탯 이름 리스트를 기반으로 존재하는 스탯을 모두 조회하고,</p>
     * <p>존재하지 않는 스탯은 자동으로 새로 생성하여 결과 리스트에 포함시킵니다.</p>
     *
     * <p>즉, 요청된 이름 중 일부가 DB에 없더라도
     * 최종 반환 리스트에는 모든 이름에 대한 {@link Stat}가 포함됩니다.</p>
     *
     * @param statNameList 조회할 스탯 이름 목록
     * @return 기존 및 신규 등록된 스탯이 모두 포함된 {@link Stat} 리스트
     */
    public List<Stat> selectStatList(List<String> statNameList) {
        // 유효한 이름만 필터링
        List<String> filteredNames = statNameList.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(String::trim)
                .toList();

        if (filteredNames.isEmpty()) {
            return new ArrayList<>();
        }

        // 1️⃣ 기존 스탯 조회
        List<Stat> statList = statRepository.findStatsByStatNameIn(filteredNames);

        // 2️⃣ 조회된 스탯 이름 집합
        Set<String> existingNames = statList.stream()
                .map(Stat::getStatName)
                .collect(Collectors.toSet());

        // 3️⃣ 존재하지 않는 스탯 이름 추출
        List<String> missingNames = statNameList.stream()
                .filter(name -> !existingNames.contains(name))
                .toList();

        // 4️⃣ 결과 리스트 구성
        List<Stat> result = new ArrayList<>(statList);

        if (!missingNames.isEmpty()) {
            result.addAll(insertStatList(missingNames));
        }

        return result;
    }

    /**
     * <p>스탯 정보를 수정합니다.</p>
     *
     * @param stat 수정할 스탯 정보 DTO
     */
    @Transactional
    public void updateStat(Stat stat) {
        Stat s = statRepository.findStatByStatCode(stat.getStatCode());
        s.setStatName(stat.getStatName());
    }

    /**
     * <p>스탯 코드를 기반으로 스탯 정보를 삭제합니다.</p>
     *
     */
    public void deleteStat(Stat stat) {
        statRepository.delete(stat);
    }
}
