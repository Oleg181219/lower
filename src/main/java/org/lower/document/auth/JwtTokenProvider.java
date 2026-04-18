package org.lower.document.auth; // Ваш пакет

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.lower.document.jooq.codegen.tables.records.UsersRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-hours}") int expirationHours) {
        // Секретный ключ должен быть достаточно длинным для HS512 (минимум 512 бит / 8 = 64 байта)
        // Keys.hmacShaKeyFor автоматически проверит длину
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationHours * 60L * 60L * 1000L;
    }

    /**
     * Генерирует JWT токен для пользователя
     */
    public String generateToken(UsersRecord user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims()
                .subject(user.getUsername())
                .add("role", user.getRole()) // Предполагаем, что role - это строка "ADMIN", "OWNER" и т.д.
                .issuedAt(now)
                .expiration(expiryDate)
                .and()
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Проверяет валидность токена
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Извлекает Claims из токена (для использования в фильтре)
     */
    public Claims getClaimsFromToken(String token) {
        return parseClaims(token);
    }

    /**
     * Парсит токен и возвращает Claims
     */
    private Claims parseClaims(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }
}