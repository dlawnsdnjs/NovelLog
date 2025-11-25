package com.example.novelcharacter.JWT;

import com.example.novelcharacter.dto.User.CustomUserDetails;
import com.example.novelcharacter.dto.User.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/static/", "/index.html",
            "/favicon.ico",
            "/manifest.json",
            "/asset-manifest.json",
            "/logo192.png",
            "/logo512.png",
            "/page",
            "/oauth/callback"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String path = request.getRequestURI();

        if (shouldNotFilter(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // permitAll 경로들을 필터에서 제외
        if (path.equals("/") ||
                path.startsWith("/api/") ||
                path.startsWith("/login") ||
                path.equals("/reissue") ||
                path.startsWith("/post/") ||
                path.startsWith("/userIdFind") ||
                path.startsWith("/resetPassword")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader("access");

        if (accessToken == null || accessToken.trim().isEmpty()) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "NO_TOKEN", "Access token is missing");
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "Access token has expired");
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!"access".equals(category)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "INVALID_TOKEN", "Invalid access token");
            return;
        }

        long uuid = jwtUtil.getUuid(accessToken);
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserDTO user = new UserDTO();
        user.setUuid(uuid);
        user.setUserName(username);
        user.setRole(role);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print("{\"error\": \"" + error + "\", \"message\": \"" + message + "\"}");
        writer.flush();
    }

    // ✅ 필터를 적용하지 않을 경로 판단
    private boolean shouldNotFilter(String requestURI) {
        return EXCLUDE_PATHS.stream()
                .anyMatch(path -> requestURI.startsWith(path) || requestURI.equals(path));
    }
}