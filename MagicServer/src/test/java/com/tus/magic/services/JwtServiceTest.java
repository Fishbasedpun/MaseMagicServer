package com.tus.magic.services;

import com.tus.magic.user_manager.models.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void testGenerateAndValidateToken() {
        String username = "alice";
        Role role = Role.USER;

        String token = jwtService.generateToken(username, role);

        assertNotNull(token, "Generated token should not be null");
        assertEquals(username, jwtService.extractUsername(token), "Username should match");
        assertTrue(jwtService.isTokenValid(token, buildUser(username)), "Token should be valid");
    }

    @Test
    void testExtractClaim() {
        String token = jwtService.generateToken("bob", Role.SYSTEM_ADMIN);
        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        assertEquals("bob", subject);
        assertEquals("SYSTEM_ADMIN", role);
    }

    private User buildUser(String username) {
        return new User(username, "pass", Collections.emptyList());
    }
}
