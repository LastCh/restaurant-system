package com.restaurant.system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-secret}")
    private String jwtRefreshSecret;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs;

    //Получение ключа подписи для Access Token
    private SecretKey getAccessTokenSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    //Получение ключа подписи для Refresh Token
    private SecretKey getRefreshTokenSigningKey() {
        return Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    //Генерация Access Token (короткоживущий)
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getAccessTokenSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    //Генерация Refresh Token (долгоживущий)
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(getRefreshTokenSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    //Получение username из Access Token
    public String getUsernameFromAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getAccessTokenSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            log.error("Error extracting username from access token: {}", e.getMessage());
            return null;
        }
    }

    //Получение username из Refresh Token
    public String getUsernameFromRefreshToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getRefreshTokenSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            log.error("Error extracting username from refresh token: {}", e.getMessage());
            return null;
        }
    }

    //Получение роли из Access Token
    public String getRoleFromAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getAccessTokenSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
        } catch (JwtException e) {
            log.error("Error extracting role from access token: {}", e.getMessage());
            return null;
        }
    }

    //Валидация Access Token
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getAccessTokenSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Access token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    //Валидация Refresh Token
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getRefreshTokenSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    //Получение времени истечения Access Token
    public long getAccessTokenExpirationMs() {
        return jwtExpirationMs;
    }

    //Получение времени истечения Refresh Token
    public long getRefreshTokenExpirationMs() {
        return jwtRefreshExpirationMs;
    }
}
