package com.example.novelcharacter.service;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.TokenResponse;
import com.example.novelcharacter.dto.User.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Refresh Token을 검증하고, 유효하면 Access Token과 Refresh Token을 재발급하는 서비스
 */
@RequiredArgsConstructor
@Service
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final UserService userService;
    private final TokenProvider tokenProvider;


    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;

        if (request.getCookies() == null) {
            return new ResponseEntity<>("no cookies", HttpStatus.BAD_REQUEST);
        }

        for (var cookie : request.getCookies()) {
            if ("Refresh".equals(cookie.getName())) {
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
        if (!"Refresh".equals(category)) {
            return new ResponseEntity<>("refresh token not valid", HttpStatus.BAD_REQUEST);
        }


        // 토큰 정보 추출
        long uuid = jwtUtil.getUuid(refresh);
        String randomId = jwtUtil.getRandomId(refresh);

        if (!refreshService.checkRefresh(uuid, randomId, refresh)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        UserDTO user = userService.getUserByUuid(uuid);
        String username = user.getUserName();
        String role = user.getRole();
        String loginType = jwtUtil.getLoginType(refresh);


        TokenResponse tokenSet = tokenProvider.generateTokenSet(uuid, username, role, loginType);
        // 새로운 토큰 발급
        // 기존 Refresh Token 삭제 후 새로 저장
        refreshService.deleteByRefresh(uuid);

        tokenProvider.sendTokens(response, tokenSet);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
