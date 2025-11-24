package com.techcorp.devops.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {
    
    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Arrange
        String message = "Authentication failed";
        
        // Act
        AuthenticationException exception = new AuthenticationException(message);
        
        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void constructor_WithMessageAndCause_ShouldCreateException() {
        // Arrange
        String message = "Authentication failed";
        Throwable cause = new RuntimeException("Invalid token");
        
        // Act
        AuthenticationException exception = new AuthenticationException(message, cause);
        
        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Invalid token", exception.getCause().getMessage());
    }
}
