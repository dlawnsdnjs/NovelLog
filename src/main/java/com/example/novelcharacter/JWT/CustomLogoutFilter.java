package com.example.novelcharacter.JWT;

import com.example.novelcharacter.service.RefreshService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {
    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshService refreshService){
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException{
        String requestUri = request.getRequestURI();
        if(!requestUri.matches("^\\/logout$")){
            filterChain.doFilter(request, response);
            return;
        }

        // CORS Preflight 요청(OPTIONS)은 무조건 통과
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
        }

        String requestMethod = request.getMethod();
        if(!requestMethod.equals("POST")){
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("Refresh")){
                refresh = cookie.getValue();
                break;
            }
        }

        if(refresh == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try{
            jwtUtil.isExpired(refresh);
        } catch(ExpiredJwtException e){
            // 이미 만료되어 로그아웃된 상태임을 알려야 함
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if(!category.equals("Refresh")){
            // refresh 쿠키에 제대로 refresh 토큰이 들어있는지 검증
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long uuid = jwtUtil.getUuid(refresh);
        String randomId = jwtUtil.getRandomId(refresh);

        // 해당 토큰이 db에 저장되어 있는지 확인
        Boolean isExist = refreshService.checkRefresh(uuid, randomId, refresh);
        if(!isExist){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        refreshService.deleteByRefresh(uuid);

        Cookie cookie = new Cookie("Refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

