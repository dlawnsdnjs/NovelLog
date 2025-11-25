package com.example.novelcharacter.controller;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.RefreshDTO;
import com.example.novelcharacter.service.RefreshService;
import com.example.novelcharacter.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * OAuth 로그인 이후 액세스 토큰과 리프레시 토큰을 동기화하고,
 * 토큰이 만료된 경우 자동으로 재발급하는 역할을 담당하는 컨트롤러입니다.
 */
@RestController
public class OAuthTokenController {

    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final ReissueService reissueService;

    @Autowired
    public OAuthTokenController(JWTUtil jwtUtil, RefreshService refreshService, ReissueService reissueService) {
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
        this.reissueService = reissueService;
    }

    @GetMapping("/api/token")
    public ResponseEntity<?> syncToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorization = null;
        String refresh = null;

        // 요청 쿠키에서 Authorization, refresh 추출
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("Authorization".equals(cookie.getName())) {
                    authorization = cookie.getValue();
                }
                if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (authorization == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String token = authorization;

        // 기존 Authorization 쿠키 제거
        response.addHeader("Set-Cookie", createResponseCookie("Authorization", null, 0).toString());

        try {
            jwtUtil.isExpired(token);
        } catch (Exception e) {
            // access 만료 시 refresh로 재발급
            if (refresh != null) {
                return reissueService.reissue(request, response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // access 유효 -> 새 access & refresh 발급
        long uuid = jwtUtil.getUuid(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        String access = jwtUtil.createJwt("access", uuid, username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", uuid, username, role, 86400000L);

        addRefreshEntity(uuid, newRefresh, 86400000L);

        // refresh 쿠키 갱신
        response.addHeader("Set-Cookie", createResponseCookie("refresh", newRefresh, 86400).toString());

        // access 토큰은 헤더로 전달
        HttpHeaders headers = new HttpHeaders();
        headers.set("Access", access);

        return ResponseEntity.ok().headers(headers).build();
    }

    private void addRefreshEntity(long uuid, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshDTO refreshDTO = new RefreshDTO();
        refreshDTO.setUuid(uuid);
        refreshDTO.setRefresh(refresh);
        refreshDTO.setExpiration(date.toString());
        refreshService.addRefresh(refreshDTO);
    }

    /**
     * ResponseCookie를 생성 (SameSite 설정 가능)
     */
    private ResponseCookie createResponseCookie(String key, String value, int maxAgeSeconds) {
        return ResponseCookie.from(key, value == null ? "" : value)
                .maxAge(maxAgeSeconds)
                .path("/")
                .httpOnly(true)
                .secure(false) // 배포 시 true 권장
                .build();
    }
}
