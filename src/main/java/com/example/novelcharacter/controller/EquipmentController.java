package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.Equipment.EquipmentDTO;
import com.example.novelcharacter.dto.Equipment.EquipmentDataDTO;
import com.example.novelcharacter.service.EquipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;

/**
 * 장비(Equipment) 관련 요청을 처리하는 컨트롤러
 * - 회차/소설과 연결된 장비 목록 조회, 등록, 수정, 삭제 등을 담당
 */
@RestController
class EquipmentController {

    private final EquipmentService equipmentService;
    private final JWTUtil jwtUtil;

    /**
     * 생성자 주입 방식으로 EquipmentService와 JWTUtil을 주입받음
     */
    @Autowired
    public EquipmentController(EquipmentService equipmentService, JWTUtil jwtUtil) {
        this.equipmentService = equipmentService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * [POST] /getEquipments
     * 특정 소설(novelNum)에 포함된 모든 장비 목록을 조회
     *
     * @param access  클라이언트로부터 전달받은 JWT Access 토큰
     * @param payload novelNum을 포함한 요청 데이터
     * @return 해당 소설의 장비 목록
     * @throws NoPermissionException  소설 접근 권한이 없는 경우 예외 발생
     */
    @PostMapping("/getEquipments")
    public List<EquipmentDTO> getEquipments(@RequestHeader String access, @RequestBody Map<String, Long> payload) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access); // JWT에서 사용자 식별자 추출
        long novelNum = payload.get("novelNum"); // 요청 데이터에서 novelNum 추출

        return equipmentService.selectEquipmentsByNovel(novelNum, uuid);
    }

    /**
     * [POST] /getEquipmentData
     * 특정 장비의 상세 데이터를 조회
     *
     * @param access  클라이언트의 JWT Access 토큰
     * @param payload equipmentNum(장비 번호)을 포함한 요청 데이터
     * @return 해당 장비의 상세 정보(스탯, 설명 등)
     * @throws NoPermissionException  장비 접근 권한이 없을 경우
     */
    @PostMapping("/getEquipmentData")
    public EquipmentDataDTO getEquipmentData(@RequestHeader String access, @RequestBody Map<String, Long> payload) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        long equipmentNum = payload.get("equipmentNum");

        return equipmentService.selectEquipmentData(uuid, equipmentNum);
    }

    /**
     * [POST] /addEquipment
     * 새 장비를 등록
     *
     * @param access           JWT Access 토큰
     * @param equipmentDataDTO 추가할 장비의 데이터
     * @throws NoPermissionException  소설 또는 회차 접근 권한이 없는 경우
     */
    @PostMapping("/addEquipment")
    public EquipmentDataDTO addEquipment(@RequestHeader String access, @Valid @RequestBody EquipmentDataDTO equipmentDataDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        equipmentService.insertEquipment(equipmentDataDTO, uuid);
        return equipmentDataDTO;
    }

    /**
     * [POST] /updateEquipment
     * 기존 장비 정보를 수정
     *
     * @param access           JWT Access 토큰
     * @param equipmentDataDTO 수정할 장비 데이터
     * @throws NoPermissionException  수정 권한이 없을 경우
     */
    @PostMapping("/updateEquipment")
    public void updateEquipment(@RequestHeader String access, @Valid @RequestBody EquipmentDataDTO equipmentDataDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);

        equipmentService.updateEquipment(equipmentDataDTO, uuid);
    }

    /**
     * [POST] /deleteEquipment
     * 특정 장비를 삭제
     *
     * @param access       JWT Access 토큰
     * @param equipmentDTO 삭제할 장비 정보 (equipmentNum 등)
     * @return HTTP 204 No Content 응답
     * @throws NoPermissionException  삭제 권한이 없을 경우
     */
    @PostMapping("/deleteEquipment")
    public ResponseEntity<Void> deleteEpisode(@RequestHeader String access, @RequestBody EquipmentDTO equipmentDTO) throws NoPermissionException {
        long uuid = jwtUtil.getUuid(access);
        equipmentService.deleteEquipment(equipmentDTO, uuid);

        return ResponseEntity.noContent().build(); // 성공 시 내용 없는 204 응답 반환
    }
}
