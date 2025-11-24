package com.techcorp.devops.controller;

import com.techcorp.devops.dto.AuthResponse;
import com.techcorp.devops.dto.LoginRequest;
import com.techcorp.devops.dto.LogoutRequest;
import com.techcorp.devops.dto.MessageResponse;
import com.techcorp.devops.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.getToken());
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }
}
