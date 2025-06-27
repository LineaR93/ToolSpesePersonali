package com.epicode.spesepersonali.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Una transazione (spesa o entrata)
 */
public class Transaction {
    private final String id;
    private final TransactionType type;
    private final double amount;
    private final String description;
    private final Category category;
    private final LocalDate date;

    public Transaction(TransactionType type, double amount, String description, 
                      Category category, LocalDate date) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    // I getter
    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public LocalDate getDate() { return date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', type=%s, amount=%.2f, description='%s', category='%s', date=%s}", 
                           id, type, amount, description, category.getName(), date);
    }
} 