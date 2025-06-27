package com.epicode.spesepersonali.factory;

import com.epicode.spesepersonali.model.Category;
import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.model.TransactionType;
import com.epicode.spesepersonali.exception.ValidationException;

import java.time.LocalDate;

/**
 * Factory per creare le transazioni
 */
public class TransactionFactory {
    
    // Crea una spesa
    public static Transaction createExpense(double amount, String description, Category category) throws ValidationException {
        return createExpense(amount, description, category, LocalDate.now());
    }
    
    public static Transaction createExpense(double amount, String description, Category category, LocalDate date) throws ValidationException {
        validateTransactionData(amount, description, category);
        
        if (amount <= 0) {
            throw new ValidationException("L'importo di una spesa deve essere positivo");
        }
        
        return new Transaction(TransactionType.EXPENSE, amount, description, category, date);
    }
    
    // Crea un'entrata
    public static Transaction createIncome(double amount, String description, Category category) throws ValidationException {
        return createIncome(amount, description, category, LocalDate.now());
    }
    
    public static Transaction createIncome(double amount, String description, Category category, LocalDate date) throws ValidationException {
        validateTransactionData(amount, description, category);
        
        if (amount <= 0) {
            throw new ValidationException("L'importo di un'entrata deve essere positivo");
        }
        
        return new Transaction(TransactionType.INCOME, amount, description, category, date);
    }
    
    // Controlla che i dati siano ok
    private static void validateTransactionData(double amount, String description, Category category) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("L'importo deve essere positivo");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("La descrizione non può essere vuota");
        }
        
        if (category == null) {
            throw new ValidationException("La categoria non può essere null");
        }
        
        if (description.length() > 255) {
            throw new ValidationException("La descrizione non può superare i 255 caratteri");
        }
    }
} 