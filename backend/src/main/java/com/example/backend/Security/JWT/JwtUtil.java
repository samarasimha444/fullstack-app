package com.example.backend.Security.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "THIS_IS_A_VERY_SECURE_SECRET_KEY_FOR_JWT_123456";

    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Long userId) {
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
}


    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
    Claims claims = validateToken(token);
    return Long.parseLong(claims.getSubject());
}

}
