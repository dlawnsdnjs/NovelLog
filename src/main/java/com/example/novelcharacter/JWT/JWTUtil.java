package com.example.novelcharacter.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {


        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public long getUuid(String token) {
        return Long.parseLong(extractClaims(token).get("uuid", String.class));
    }

    public String getRandomId(String token) {
        return extractClaims(token).get("randomId", String.class);
    }

    public String getUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }

    public String getRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            if (expiration.before(new Date())) {
                throw new JwtException("Expired JWT token");
            }
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // 이미 만료된 토큰
            throw new JwtException("Expired JWT token");
        }
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료되었지만 claims는 가져오기
        }
    }

    public String createJwt(long uuid, String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("uuid", String.valueOf(uuid))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createJwt(String category, long uuid, String username, String role, String loginType, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("uuid", String.valueOf(uuid))
                .claim("username", username)
                .claim("role", role)
                .claim("loginType", loginType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createJwt(String category, long uuid, String randomId, String loginType, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("uuid", String.valueOf(uuid))
                .claim("randomId", randomId)
                .claim("loginType", loginType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String getCategory(String token) {
        return extractClaims(token).get("category", String.class);
    }

    public String getLoginType(String token) {
        return extractClaims(token).get("loginType", String.class);
    }
}
