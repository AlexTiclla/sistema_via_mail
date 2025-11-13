package com.example.sistema_via_mail.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Excepción para errores de validación
 */
public class ValidationException extends RuntimeException {
    
    private List<String> validationErrors;
    
    public ValidationException(String message) {
        super(message);
        this.validationErrors = new ArrayList<>();
    }
    
    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = new ArrayList<>();
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
    
    public void addValidationError(String error) {
        this.validationErrors.add(error);
    }
    
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
    
    public String getAllErrorsAsString() {
        return String.join("; ", validationErrors);
    }
}

