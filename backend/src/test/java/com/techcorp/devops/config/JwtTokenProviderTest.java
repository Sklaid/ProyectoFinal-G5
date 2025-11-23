package com.techcorp.devops.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {
    
    private JwtTokenProvider jwtTokenProvider;
    private String jwtSecret;
    private long jwtExpirationMs;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtSecret = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";
        jwtExpirationMs = 86400000; // 24 hours
        
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", jwtExpirationMs);
    }
    
    @Test
    void generateToken_WithAuthentication_ShouldReturnValidToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        // Act
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }
    
    @Test
    void generateToken_WithUsername_ShouldReturnValidToken() {
        // Arrange
        String username = "testuser";
        
        // Act
        String token = jwtTokenProvider.generateToken(username);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }
    
    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtTokenProvider.generateToken(username);
        
        // Act
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        
        // Assert
        assertEquals(username, extractedUsername);
    }
    
    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtTokenProvider.generateToken("testuser");
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void validateToken_WithInvalidSignature_ShouldReturnFalse() {
        // Arrange
        String token = jwtTokenProvider.generateToken("testuser");
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(tamperedToken);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", -1000L);
        String expiredToken = jwtTokenProvider.generateToken("testuser");
        
        // Reset expiration for validation
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", jwtExpirationMs);
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Arrange
        String emptyToken = "";
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Arrange
        String nullToken = null;
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(nullToken);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void generateToken_ShouldIncludeExpirationDate() {
        // Arrange
        String username = "testuser";
        long beforeGeneration = System.currentTimeMillis();
        
        // Act
        String token = jwtTokenProvider.generateToken(username);
        
        // Assert
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date expiration = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        
        long expectedExpiration = beforeGeneration + jwtExpirationMs;
        assertTrue(expiration.getTime() >= expectedExpiration - 1000); // Allow 1 second tolerance
        assertTrue(expiration.getTime() <= expectedExpiration + 1000);
    }
    
    @Test
    void generateToken_ShouldIncludeSubject() {
        // Arrange
        String username = "testuser";
        
        // Act
        String token = jwtTokenProvider.generateToken(username);
        String extractedUsername = jwtTokenProvider.getUsernameFromToken(token);
        
        // Assert
        assertEquals(username, extractedUsername);
    }
}
