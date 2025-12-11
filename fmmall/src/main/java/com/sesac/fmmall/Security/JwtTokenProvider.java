package com.sesac.fmmall.Security;

import com.sesac.fmmall.Entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:change-this-secret-key}")
    private String secretKeyPlain;

    @Value("${jwt.token-validity-in-seconds:3600}")
    private long tokenValidityInSeconds;

    private Key secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyPlain.getBytes());
    }

    public String createToken(User user) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + tokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(user.getLoginId())
                .claim("role", user.getRole().name())
                .claim("userId", user.getUserId())
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String getLoginId(String token) {
        return parseClaims(token).getSubject();
    }


    public int getUserId(String token) {
        return parseClaims(token).get("userId", Integer.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
