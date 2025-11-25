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
        
        // Cleanup before test to avoid unique constraint violations
        userRepository.deleteAll();
        
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
        
        // Cleanup before test to avoid unique constraint violations
        userRepository.deleteAll();
        
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
        
        // Cleanup before test to avoid unique constraint violations
        userRepository.deleteAll();
        
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
    
    /**
     * Feature: devops-enterprise-platform, Property 4: Logout invalidates session
     */
    @Property(tries = 100)
    public void logout_ShouldInvalidateToken(
            @ForAll("validUserCredentials") ValidUserCredentials credentials) {
        
        // Cleanup before test to avoid unique constraint violations
        userRepository.deleteAll();
        
        try {
            // Arrange: Create user and login to get a valid token
            User user = User.builder()
                    .username(credentials.getUsername())
                    .password(passwordEncoder.encode(credentials.getPassword()))
                    .email(credentials.getEmail())
                    .role(Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user);
            
            LoginRequest loginRequest = new LoginRequest(credentials.getUsername(), credentials.getPassword());
            AuthResponse authResponse = authService.login(loginRequest);
            String token = authResponse.getToken();
            
            // Verify token is valid and not blacklisted before logout
            assertTrue(tokenProvider.validateToken(token), 
                    "Token should be valid before logout");
            
            // Act: Logout
            authService.logout(token);
            
            // Assert: Token should be blacklisted after logout
            assertTrue(authService.isTokenBlacklisted(token), 
                    "Token should be blacklisted after logout");
            
            // Additional assertion: Even though token is technically valid (not expired),
            // it should be rejected because it's blacklisted
            assertTrue(tokenProvider.validateToken(token), 
                    "Token should still be technically valid (not expired)");
            assertTrue(authService.isTokenBlacklisted(token), 
                    "But token should be in blacklist, preventing authentication");
        } finally {
            // Cleanup
            userRepository.deleteAll();
        }
    }
    
    /**
     * Feature: devops-enterprise-platform, Property 5: Passwords are securely hashed
     */
    @Property(tries = 100)
    public void storedPasswords_ShouldBeSecurelyHashed(
            @ForAll("validUserCredentials") ValidUserCredentials credentials) {
        
        // Cleanup before test to avoid unique constraint violations
        userRepository.deleteAll();
        
        try {
            // Arrange: Create user with plaintext password
            String plaintextPassword = credentials.getPassword();
            
            User user = User.builder()
                    .username(credentials.getUsername())
                    .password(passwordEncoder.encode(plaintextPassword))
                    .email(credentials.getEmail())
                    .role(Role.USER)
                    .active(true)
                    .build();
            
            // Act: Save user to database
            User savedUser = userRepository.save(user);
            
            // Assert 1: Stored password should not match plaintext
            assertNotEquals(plaintextPassword, savedUser.getPassword(),
                    "Stored password should not match plaintext password");
            
            // Assert 2: Stored password should start with BCrypt prefix ($2a$ or $2b$ or $2y$)
            assertTrue(savedUser.getPassword().startsWith("$2a$") || 
                       savedUser.getPassword().startsWith("$2b$") ||
                       savedUser.getPassword().startsWith("$2y$"),
                    "Stored password should have BCrypt hash prefix");
            
            // Assert 3: Stored password should be at least 60 characters (BCrypt hash length)
            assertTrue(savedUser.getPassword().length() >= 60,
                    "BCrypt hash should be at least 60 characters long");
            
            // Assert 4: Password encoder should be able to match plaintext with hash
            assertTrue(passwordEncoder.matches(plaintextPassword, savedUser.getPassword()),
                    "Password encoder should be able to verify plaintext against stored hash");
            
            // Assert 5: Different plaintext should not match the hash
            assertFalse(passwordEncoder.matches(plaintextPassword + "wrong", savedUser.getPassword()),
                    "Different plaintext should not match the stored hash");
            
        } finally {
            // Cleanup
            userRepository.deleteAll();
        }
    }
}
