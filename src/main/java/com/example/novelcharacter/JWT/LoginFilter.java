    package com.example.novelcharacter.JWT;

    import com.example.novelcharacter.dto.TokenResponse;
    import com.example.novelcharacter.dto.User.CustomUserDetails;
    import com.example.novelcharacter.dto.User.UserDTO;
    import com.example.novelcharacter.service.TokenProvider;
    import com.example.novelcharacter.service.UserService;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import java.util.Collection;
    import java.util.Iterator;

    @RequiredArgsConstructor
    public class LoginFilter extends UsernamePasswordAuthenticationFilter {

        private final AuthenticationManager authenticationManager;
        private final UserService userService;
        private final TokenProvider tokenProvider;

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

            TokenResponse tokenSet = tokenProvider.generateTokenSet(uuid, username, role, "LOCAL");
            tokenProvider.sendTokens(response, tokenSet);

            response.setStatus(HttpStatus.OK.value());
        }

        // 로그인 실패 시
        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
