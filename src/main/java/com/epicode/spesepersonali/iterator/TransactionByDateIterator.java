package com.epicode.spesepersonali.iterator;

import com.epicode.spesepersonali.model.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Vede le transazioni per data
 */
public class TransactionByDateIterator implements TransactionIterator {
    
    private final List<Transaction> transactions;
    private int position;
    
    public TransactionByDateIterator(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
        // Ordina per data (più recenti prima)
        this.transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        this.position = 0;
    }
    
    @Override
    public boolean hasNext() {
        return position < transactions.size();
    }
    
    @Override
    public Transaction next() {
        if (!hasNext()) {
            return null;
        }
        
        return transactions.get(position++);
    }
    
    @Override
    public void reset() {
        position = 0;
    }
    
    @Override
    public int getCurrentPosition() {
        return position;
    }
    
    @Override
    public int getTotalCount() {
        return transactions.size();
    }
} 