package com.techcorp.devops.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler handler;
    
    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }
    
    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        EntityNotFoundException exception = new EntityNotFoundException("Employee not found");
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            handler.handleEntityNotFoundException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getCode());
        assertEquals("Employee not found", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
        assertNotNull(response.getBody().getTimestamp());
    }
    
    @Test
    void handleValidationException_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        List<ValidationException.ValidationError> errors = Arrays.asList(
            new ValidationException.ValidationError("email", "Invalid email format"),
            new ValidationException.ValidationError("firstName", "First name is required")
        );
        ValidationException exception = new ValidationException("Validation failed", errors);
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            handler.handleValidationException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("email", response.getBody().getErrors().get(0).getField());
        assertEquals("Invalid email format", response.getBody().getErrors().get(0).getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
    
    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("employeeDTO", "email", "must be a valid email");
        FieldError fieldError2 = new FieldError("employeeDTO", "firstName", "must not be blank");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            handler.handleMethodArgumentNotValid(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("Invalid request data", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("email", response.getBody().getErrors().get(0).getField());
        assertEquals("must be a valid email", response.getBody().getErrors().get(0).getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
    
    @Test
    void handleAuthenticationException_ShouldReturnUnauthorizedResponse() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Invalid credentials");
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            handler.handleAuthenticationException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UNAUTHORIZED", response.getBody().getCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
        assertNotNull(response.getBody().getTimestamp());
    }
    
    @Test
    void handleAccessDeniedException_ShouldReturnForbiddenResponse() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            handler.handleAccessDeniedException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("FORBIDDEN", response.getBody().getCode());
        assertEquals("Access denied", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
        assertNotNull(response.getBody().getTimestamp());
    }
    
    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");
        
        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            handler.handleGenericException(exception);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().isEmpty());
        assertNotNull(response.getBody().getTimestamp());
    }
}
