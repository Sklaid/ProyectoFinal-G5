package com.techcorp.devops.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Error code constants to avoid duplication
    private static final String ERROR_CODE_UNAUTHORIZED = "UNAUTHORIZED";
    private static final String ERROR_CODE_VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String ERROR_CODE_NOT_FOUND = "NOT_FOUND";
    private static final String ERROR_CODE_FORBIDDEN = "FORBIDDEN";
    private static final String ERROR_CODE_INTERNAL_ERROR = "INTERNAL_ERROR";
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_NOT_FOUND,
                ex.getMessage(),
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();
        for (ValidationException.ValidationError error : ex.getErrors()) {
            fieldErrors.add(new FieldError(error.getField(), error.getMessage()));
        }
        
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_VALIDATION_ERROR,
                ex.getMessage(),
                fieldErrors,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.add(new FieldError(error.getField(), error.getDefaultMessage()))
        );
        
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_VALIDATION_ERROR,
                "Invalid request data",
                fieldErrors,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(com.techcorp.devops.exception.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(com.techcorp.devops.exception.AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_UNAUTHORIZED,
                ex.getMessage(),
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    // Handle Spring Security BadCredentialsException (Bug #1 fix)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_UNAUTHORIZED,
                "Invalid username or password",
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    // Handle Spring Security AuthenticationException (Bug #1 fix)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpringAuthenticationException(AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_UNAUTHORIZED,
                "Authentication failed",
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    // Handle invalid enum values and malformed JSON (Bug #2 fix)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request data";
        
        // Check if it's an enum conversion error
        if (ex.getMessage() != null && ex.getMessage().contains("Cannot deserialize value")) {
            message = "Invalid enum value in request";
        }
        
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_VALIDATION_ERROR,
                message,
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_FORBIDDEN,
                "Access denied",
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                ERROR_CODE_INTERNAL_ERROR,
                "An unexpected error occurred",
                new ArrayList<>(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
        private List<FieldError> errors;
        private LocalDateTime timestamp;
    }
    
    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
