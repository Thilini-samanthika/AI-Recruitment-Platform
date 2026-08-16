package com.recruitment.gateway.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    private String generateTestToken(Long userId, String email, String role, long validityMs) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Test
    void shouldValidateValidToken() {
        String token = generateTestToken(10L, "recruiter@company.com", "ROLE_COMPANY", 3600000);

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("recruiter@company.com", jwtUtil.extractEmail(token));
        assertEquals("ROLE_COMPANY", jwtUtil.extractRole(token));
        assertEquals(10L, jwtUtil.extractUserId(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void shouldRejectExpiredToken() {
        String expiredToken = generateTestToken(10L, "recruiter@company.com", "ROLE_COMPANY", -1000);
        assertFalse(jwtUtil.validateToken(expiredToken));
    }
}
