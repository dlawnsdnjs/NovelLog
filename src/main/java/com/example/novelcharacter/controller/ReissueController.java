package com.example.novelcharacter.controller;

import com.example.novelcharacter.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT 토큰 재발급 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>Access Token이 만료되었을 때, 유효한 Refresh Token을 이용하여
 * 새로운 Access Token 및 Refresh Token을 재발급하는 기능을 제공합니다.</p>
 *
 * <p>요청 시 클라이언트의 쿠키에서 Refresh Token을 추출하여 검증하고,
 * 검증이 성공하면 {@link ReissueService}를 통해 새로운 토큰을 생성하여 응답합니다.</p>
 */
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    /**
     * 만료된 Access Token을 새로운 토큰으로 재발급합니다.
     *
     * <p>Refresh Token을 검증하여 유효할 경우 새 Access Token을 발급하고,
     * 쿠키 및 응답 헤더에 재설정합니다.</p>
     *
     * @param request  클라이언트의 HTTP 요청 (쿠키에서 Refresh Token을 추출)
     * @param response 클라이언트에게 새로운 토큰을 전달하기 위한 HTTP 응답 객체
     * @return 재발급 결과를 포함한 {@link ResponseEntity} 객체 (성공 시 200 OK)
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissue(request, response);
    }
}
