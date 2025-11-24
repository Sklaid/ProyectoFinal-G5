package com.techcorp.devops.service;

import com.techcorp.devops.config.JwtTokenProvider;
import com.techcorp.devops.dto.AuthResponse;
import com.techcorp.devops.dto.LoginRequest;
import com.techcorp.devops.dto.UserDTO;
import com.techcorp.devops.entity.User;
import com.techcorp.devops.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    
    // In-memory blacklist for invalidated tokens (in production, use Redis)
    private final Set<String> tokenBlacklist = new HashSet<>();
    
    public AuthService(AuthenticationManager authenticationManager, 
                      JwtTokenProvider tokenProvider, 
                      UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }
    
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        // Update last login time
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        
        return AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();
    }
    
    public void logout(String token) {
        // Add token to blacklist
        tokenBlacklist.add(token);
        SecurityContextHolder.clearContext();
    }
    
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}
