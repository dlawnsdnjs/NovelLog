    package com.example.novelcharacter.JWT;

    import com.example.novelcharacter.dto.User.CustomUserDetails;
    import com.example.novelcharacter.dto.RefreshDTO;
    import com.example.novelcharacter.dto.User.UserDTO;
    import com.example.novelcharacter.service.RefreshService;
    import com.example.novelcharacter.service.UserService;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseCookie;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import java.util.Collection;
    import java.util.Date;
    import java.util.Iterator;

    @RequiredArgsConstructor
    public class LoginFilter extends UsernamePasswordAuthenticationFilter {

        private final AuthenticationManager authenticationManager;
        private final JWTUtil jwtUtil;
        private final RefreshService refreshService;
        private final UserService userService;

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

            // 클라이언트 요청에서 username, password 추출
            String username = obtainUsername(request);
            String password = obtainPassword(request);


            // 인증 토큰 생성 후 AuthenticationManager에 전달
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password, null);

            return authenticationManager.authenticate(authToken);
        }

        // ✅ 로그인 성공 시 JWT 생성 및 쿠키/헤더 전송
        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            long uuid = customUserDetails.getUuid();
            String username = customUserDetails.getUsername();
            UserDTO userDTO = userService.getUserByUuid(uuid);
            userService.updateLastLoginTime(userDTO);

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();
            String role = auth.getAuthority();

            // ✅ JWT 토큰 생성
            String access = jwtUtil.createJwt("access", uuid, username, role, "LOCAL", 600000L);     // 10분
            String refresh = jwtUtil.createJwt("refresh", uuid, username, role, "LOCAL", 86400000L); // 24시간

            // DB에 refresh 저장
            addRefreshDTO(uuid, refresh, 86400000L);

            // ✅ access 토큰은 헤더로 전송
            response.setHeader("Access", access);

            // ✅ refresh 토큰은 ResponseCookie로 설정 (보안, CORS 대응)
            ResponseCookie refreshCookie = ResponseCookie.from("refresh", refresh)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();

            response.addHeader("Set-Cookie", refreshCookie.toString());
            response.setStatus(HttpStatus.OK.value());
        }

        // 로그인 실패 시
        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        // ✅ refresh 토큰 DB 저장
        private void addRefreshDTO(long uuid, String refresh, Long expiredMs) {
            Date date = new Date(System.currentTimeMillis() + expiredMs);

            RefreshDTO refreshDTO = new RefreshDTO();
            refreshDTO.setUuid(uuid);
            refreshDTO.setRefresh(refresh);
            refreshDTO.setExpiration(date.toString());

            refreshService.addRefresh(refreshDTO);
        }
    }
