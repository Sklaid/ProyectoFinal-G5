package com.techcorp.devops.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    
    private final List<ValidationError> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }
    
    public ValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors;
    }
    
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;
        
        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
