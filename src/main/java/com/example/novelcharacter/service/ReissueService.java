package com.example.novelcharacter.service;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.RefreshDTO;
import com.example.novelcharacter.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Refresh Token을 검증하고, 유효하면 Access Token과 Refresh Token을 재발급하는 서비스
 */
@Service
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final UserService userService;

    @Autowired
    public ReissueService(JWTUtil jwtUtil, RefreshService refreshService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
        this.userService = userService;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;

        if (request.getCookies() == null) {
            return new ResponseEntity<>("no cookies", HttpStatus.BAD_REQUEST);
        }

        for (var cookie : request.getCookies()) {
            if ("refresh".equals(cookie.getName())) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            return new ResponseEntity<>("refresh token not valid", HttpStatus.BAD_REQUEST);
        }

        if (!refreshService.existsByRefresh(refresh)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 토큰 정보 추출
        long uuid = jwtUtil.getUuid(refresh);
        UserDTO user = userService.getUserByUuid(uuid);
        String username = user.getUserName();
        String role = user.getRole();

        // 새로운 토큰 발급
        String newAccess = jwtUtil.createJwt("access", uuid, username, role, 600_000L);      // 10분
        String newRefresh = jwtUtil.createJwt("refresh", uuid, username, role, 86_400_000L); // 24시간

        // 기존 Refresh Token 삭제 후 새로 저장
        refreshService.deleteByRefresh(refresh);
        addRefreshEntity(uuid, newRefresh, 86_400_000L);

        // 응답에 새 토큰 추가
        response.setHeader("access", newAccess);

        // ✅ ResponseCookie 사용
        ResponseCookie refreshCookie = ResponseCookie.from("refresh", newRefresh)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60) // 1일
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    private void addRefreshEntity(long uuid, String newRefresh, Long expires) {
        Date date = new Date(System.currentTimeMillis() + expires);

        RefreshDTO refreshEntity = new RefreshDTO();
        refreshEntity.setUuid(uuid);
        refreshEntity.setRefresh(newRefresh);
        refreshEntity.setExpiration(date.toString());

        refreshService.addRefresh(refreshEntity);
    }
}
