package com.techcorp.devops.property;

import com.techcorp.devops.config.JwtTokenProvider;
import com.techcorp.devops.dto.AuthResponse;
import com.techcorp.devops.dto.LoginRequest;
import com.techcorp.devops.entity.Role;
import com.techcorp.devops.entity.User;
import com.techcorp.devops.repository.UserRepository;
import com.techcorp.devops.service.AuthService;
import net.jqwik.api.*;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Property-based tests for authentication module
 * Feature: devops-enterprise-platform
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@JqwikSpringSupport
public class AuthenticationPropertiesTest {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private MockMvc mockMvc;
    
    /**
     * Feature: devops-enterprise-platform, Property 1: Valid credentials authenticate successfully
     */
    @Property(tries = 100)
    public void validCredentials_ShouldAuthenticateSuccessfully(
            @ForAll("validUserCredentials") ValidUserCredentials credentials) {
        
        try {
            // Arrange: Create user with hashed password
            User user = User.builder()
                    .username(credentials.getUsername())
                    .password(passwordEncoder.encode(credentials.getPassword()))
                    .email(credentials.getEmail())
                    .role(Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user);
            
            LoginRequest loginRequest = new LoginRequest(credentials.getUsername(), credentials.getPassword());
            
            // Act
            AuthResponse response = authService.login(loginRequest);
            
            // Assert
            assertNotNull(response.getToken(), "Token should not be null");
            assertNotNull(response.getUser(), "User should not be null");
            assertEquals(credentials.getUsername(), response.getUser().getUsername(), "Username should match");
            assertTrue(tokenProvider.validateToken(response.getToken()), "Token should be valid");
            assertEquals(credentials.getUsername(), 
                    tokenProvider.getUsernameFromToken(response.getToken()),
                    "Username from token should match");
        } finally {
            // Cleanup
            userRepository.deleteAll();
        }
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 2: Invalid credentials are rejected
     */
    @Property(tries = 100)
    public void invalidCredentials_ShouldBeRejected(
            @ForAll("validUserCredentials") ValidUserCredentials validCreds,
            @ForAll("invalidCredentials") String invalidPassword) {
        
        try {
            // Arrange: Create user with valid credentials
            User user = User.builder()
                    .username(validCreds.getUsername())
                    .password(passwordEncoder.encode(validCreds.getPassword()))
                    .email(validCreds.getEmail())
                    .role(Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user);
            
            // Try to login with wrong password
            LoginRequest loginRequest = new LoginRequest(validCreds.getUsername(), invalidPassword);
            
            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest);
            }, "Login with invalid password should throw BadCredentialsException");
        } finally {
            // Cleanup
            userRepository.deleteAll();
        }
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 2: Invalid credentials are rejected
     * Test with non-existent username
     */
    @Property(tries = 100)
    public void nonExistentUsername_ShouldBeRejected(
            @ForAll("validUserCredentials") ValidUserCredentials credentials,
            @ForAll("nonExistentUsername") String nonExistentUsername) {
        
        try {
            // Arrange: Create user with valid credentials
            User user = User.builder()
                    .username(credentials.getUsername())
                    .password(passwordEncoder.encode(credentials.getPassword()))
                    .email(credentials.getEmail())
                    .role(Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user);
            
            // Try to login with non-existent username
            LoginRequest loginRequest = new LoginRequest(nonExistentUsername, credentials.getPassword());
            
            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest);
            }, "Login with non-existent username should throw BadCredentialsException");
        } finally {
            // Cleanup
            userRepository.deleteAll();
        }
    }
    
    @Provide
    Arbitrary<ValidUserCredentials> validUserCredentials() {
        Arbitrary<String> usernames = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(5)
                .ofMaxLength(20);
        
        Arbitrary<String> passwords = Arbitraries.strings()
                .withCharRange('a', 'z')
                .numeric()
                .ofMinLength(8)
                .ofMaxLength(30);
        
        Arbitrary<String> emails = usernames.map(name -> name + "@example.com");
        
        return Combinators.combine(usernames, passwords, emails)
                .as(ValidUserCredentials::new);
    }
    
    @Provide
    Arbitrary<String> invalidCredentials() {
        // Generate passwords that are different from valid ones
        return Arbitraries.strings()
                .withCharRange('A', 'Z')
                .numeric()
                .withChars('!', '@', '#', '$')
                .ofMinLength(8)
                .ofMaxLength(30);
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 3: Protected resources require valid authentication
     */
    @Property(tries = 100)
    public void protectedEndpoints_WithoutToken_ShouldReturn401(
            @ForAll("protectedEndpoint") String endpoint) throws Exception {
        
        // Act: Attempt to access protected endpoint without Authorization header
        mockMvc.perform(get(endpoint))
                // Assert: Should return 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 3: Protected resources require valid authentication
     * Test with invalid token
     */
    @Property(tries = 100)
    public void protectedEndpoints_WithInvalidToken_ShouldReturn401(
            @ForAll("protectedEndpoint") String endpoint,
            @ForAll("invalidToken") String invalidToken) throws Exception {
        
        // Act: Attempt to access protected endpoint with invalid token
        mockMvc.perform(get(endpoint)
                        .header("Authorization", "Bearer " + invalidToken))
                // Assert: Should return 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }
    
    @Provide
    Arbitrary<String> protectedEndpoint() {
        // Generate various protected endpoint paths that should require authentication
        return Arbitraries.of(
                "/api/employees",
                "/api/employees/1",
                "/api/employees/999",
                "/api/users",
                "/api/users/profile",
                "/api/admin/settings",
                "/api/data/export",
                "/api/reports/summary"
        );
    }
    
    @Provide
    Arbitrary<String> invalidToken() {
        // Generate invalid JWT tokens
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .numeric()
                .withChars('.', '-', '_')
                .ofMinLength(20)
                .ofMaxLength(100);
    }
    
    @Provide
    Arbitrary<String> nonExistentUsername() {
        // Generate usernames that are unlikely to exist
        return Arbitraries.strings()
                .withCharRange('A', 'Z')
                .numeric()
                .withChars('_', '-')
                .ofMinLength(10)
                .ofMaxLength(25);
    }
}
