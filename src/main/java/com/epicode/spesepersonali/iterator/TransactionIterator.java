package com.epicode.spesepersonali.iterator;

import com.epicode.spesepersonali.model.Transaction;

/**
 * Interfaccia per vedere le transazioni
 */
public interface TransactionIterator {
    
    boolean hasNext();
    
    Transaction next();
    
    void reset();
    
    int getCurrentPosition();
    
    int getTotalCount();
} 