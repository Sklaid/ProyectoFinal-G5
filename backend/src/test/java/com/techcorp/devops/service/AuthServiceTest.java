package com.techcorp.devops.service;

import com.techcorp.devops.config.JwtTokenProvider;
import com.techcorp.devops.dto.AuthResponse;
import com.techcorp.devops.dto.LoginRequest;
import com.techcorp.devops.entity.Role;
import com.techcorp.devops.entity.User;
import com.techcorp.devops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("$2a$10$hashedpassword")
                .email("test@example.com")
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        loginRequest = new LoginRequest("testuser", "password123");
    }
    
    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        String expectedToken = "jwt.token.here";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(expectedToken);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        AuthResponse response = authService.login(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
        assertEquals("test@example.com", response.getUser().getEmail());
        assertEquals(Role.USER, response.getUser().getRole());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        
        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, never()).generateToken(any(Authentication.class));
        verify(userRepository, never()).findByUsername(anyString());
    }
    
    @Test
    void login_ShouldUpdateLastLoginTime() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        LocalDateTime beforeLogin = LocalDateTime.now();
        
        // Act
        authService.login(loginRequest);
        
        // Assert
        verify(userRepository).save(argThat(user -> 
            user.getLastLogin() != null && 
            !user.getLastLogin().isBefore(beforeLogin)
        ));
    }
    
    @Test
    void login_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        
        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void logout_ShouldAddTokenToBlacklist() {
        // Arrange
        String token = "jwt.token.here";
        
        // Act
        authService.logout(token);
        
        // Assert
        assertTrue(authService.isTokenBlacklisted(token));
    }
    
    @Test
    void isTokenBlacklisted_WithBlacklistedToken_ShouldReturnTrue() {
        // Arrange
        String token = "blacklisted.token";
        authService.logout(token);
        
        // Act
        boolean result = authService.isTokenBlacklisted(token);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void isTokenBlacklisted_WithValidToken_ShouldReturnFalse() {
        // Arrange
        String token = "valid.token";
        
        // Act
        boolean result = authService.isTokenBlacklisted(token);
        
        // Assert
        assertFalse(result);
    }
}
