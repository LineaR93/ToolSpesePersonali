package com.epicode.spesepersonali.exception;

/**
 * Exception Shielding - Eccezione di dominio per errori di accesso ai dati
 */
public class DataAccessException extends Exception {
    
    public DataAccessException(String message) {
        super(message);
    }
    
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
} 