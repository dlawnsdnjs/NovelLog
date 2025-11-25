package com.example.novelcharacter.OAuth2;

import com.example.novelcharacter.JWT.JWTUtil;
import com.example.novelcharacter.dto.CustomOAuth2User;
import com.example.novelcharacter.dto.UserDTO;
import com.example.novelcharacter.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserService userService;

    public CustomSuccessHandler(JWTUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // ✅ OAuth2User 정보 추출
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        long userId = customUserDetails.getUuid();
        String username = customUserDetails.getName();
        UserDTO userDTO = userService.getUserByUuid(userId);
        // userService.updateLastLoginTime(userDTO);

        // ✅ 권한 정보
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // ✅ JWT 생성
        String token = jwtUtil.createJwt(userId, username, role, 60 * 60 * 60L);

        // ✅ 쿠키 생성 및 추가
        ResponseCookie cookie = ResponseCookie.from("Authorization", token)
                .httpOnly(true)
                .secure(false)          // 항상 HTTPS 전송
                .path("/")
                .maxAge(60 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        // ✅ 프론트엔드로 리다이렉트
        response.sendRedirect("/oauth/callback");
    }
}
