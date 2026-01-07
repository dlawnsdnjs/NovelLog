package com.example.novelcharacter.controller;

import com.example.novelcharacter.dto.User.JoinDTO;
import com.example.novelcharacter.service.JoinService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Profile("dev")
@RequestMapping("/api")
public class JoinTestController {
    private final JoinService joinService;

    /**
     * [POST] /api/join
     * 사용자가 입력한 회원가입 정보를 바탕으로 회원가입 처리
     *
     * @param joinDTO  클라이언트가 전달한 회원가입 정보 (id, password, email, authCode 등)
     * @return 가입 성공 시 "ok" 문자열 반환
     *
     * 처리 과정:
     * - 아이디/이메일 중복 검사
     * - 이메일 인증코드 검증
     * - 비밀번호 암호화 후 DB 저장
     */
    @PostMapping("/join")
    public String joinProcess(@Valid @RequestBody JoinDTO joinDTO) {
        System.out.println("testJoin : "+joinDTO);
        joinService.joinProcess(joinDTO);
        return "ok";
    }

    /**
     * [POST] /api/emailVerify
     * 회원가입 시 사용자의 이메일로 인증코드를 전송하는 API
     *
     * @param request  이메일 주소를 담은 요청 객체
     * @return 전송 성공 시 HTTP 200 + "ok"
     * @throws MessagingException  메일 전송 실패 시 발생
     *
     * 처리 과정:
     * - 사용자가 입력한 이메일로 인증코드 발송
     * - 코드 및 이메일을 서버(예: Redis나 Map)에 임시 저장
     */
    @PostMapping("/emailVerify")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody JoinController.EmailRequest request) throws MessagingException {
        String email = request.getEmail();

        String code = joinService.sendCodeToEmail(email); // 이메일 발송 서비스 호출

        return ResponseEntity.ok(code);
    }

    /**
     * [POST] /api/codeVerify
     * 사용자가 입력한 이메일과 인증코드가 일치하는지 검증
     *
     * @param request  이메일 + 인증코드 정보
     * @return 인증 성공 시 "ok", 실패 시 "code incorrect"
     *
     * 처리 과정:
     * - 서버에 저장된 인증코드와 비교
     * - 일치하면 인증 완료로 처리
     */
    @PostMapping("/codeVerify")
    public ResponseEntity<?> codeVerify(@RequestBody JoinController.CodeRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        String joinToken = joinService.verifiedCode(email, code);

        return ResponseEntity.ok(Map.of("token", joinToken));
    }

    /**
     * 이메일 인증 요청 시 사용되는 DTO
     * - 클라이언트가 이메일 주소를 담아 요청
     */
    @Data
    public static class EmailRequest {
        private String email;
    }

    /**
     * 인증코드 검증 요청 시 사용되는 DTO
     * - 이메일과 인증코드(code)를 함께 전달
     */
    @Data
    public static class CodeRequest {
        private String email;
        private String code;
    }
}
