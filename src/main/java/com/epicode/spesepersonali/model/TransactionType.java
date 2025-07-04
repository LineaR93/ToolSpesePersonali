package com.epicode.spesepersonali.model;

/**
 * Tipi di transazione
 */
public enum TransactionType {
    INCOME("Entrata"),
    EXPENSE("Spesa");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 