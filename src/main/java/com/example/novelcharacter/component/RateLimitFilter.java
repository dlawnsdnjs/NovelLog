package com.example.novelcharacter.component;

import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // IP별로 Bucket 관리 (메모리 기반)
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    // RateLimit 적용 대상 API 목록
    private static final String[] PROTECTED_PATHS = {
            "/userIdFind",
            "/resetPassword",
            "/emailVerify"
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // RateLimit을 적용해야 하는 API가 아니면 그냥 진행
        if (!shouldRateLimit(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIP(request);

        Bucket bucket = bucketCache.computeIfAbsent(ip, key -> createBucket());

        // 요청 1회 소비
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // 제한 초과 → 429
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }

    // 보호 대상 URL인지 검사
    private boolean shouldRateLimit(String uri) {
        for (String path : PROTECTED_PATHS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    private Bucket createBucket() {
        // 1분마다 3개 토큰을 재충전하고, 최대 용량 3개
        Bandwidth limit = Bandwidth.classic(
                3, // capacity
                Refill.greedy(3, Duration.ofMinutes(1)) // refill
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // IP 추출 (X-Forwarded-For 고려)
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}