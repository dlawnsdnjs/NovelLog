package com.example.novelcharacter.service;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenProvider {
    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;

    // ✅ 토큰 발급 + DB 저장 통합 관리
    public TokenResponse generateTokenSet(long uuid, String username, String role, String loginType) {
        String randomId = UUID.randomUUID().toString();
        String access = jwtUtil.createJwt("Access", uuid, username, role, loginType, 600000L);      // 10분
        String refresh = jwtUtil.createJwt("Refresh", uuid, randomId, loginType, 86400000L); // 24시간

        saveRefreshInDB(uuid, randomId, refresh, 86400000L);

        return new TokenResponse(access, refresh);
    }

    // ✅ 공통 응답 설정 (헤더 + 쿠키)
    public void sendTokens(HttpServletResponse response, TokenResponse tokenResponse) {
        response.setHeader("Access", tokenResponse.getAccessToken());

        ResponseCookie cookie = ResponseCookie.from("Refresh", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void saveRefreshInDB(long uuid, String randomId, String refresh, long expiredMs) {
        refreshService.addRefresh(uuid, randomId, refresh, expiredMs);
    }
}
