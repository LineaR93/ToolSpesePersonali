package com.epicode.spesepersonali.exception;

/**
 * Exception Shielding - Eccezione di dominio per errori di validazione
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 