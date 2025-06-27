package com.epicode.spesepersonali.strategy;

import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.model.TransactionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcola totali semplici
 */
public class MonthlyTotalStrategy implements CalculationStrategy {
    
    @Override
    public Map<String, Object> calculate(List<Transaction> transactions) {
        Map<String, Object> results = new HashMap<>();
        
        double totaleEntrate = 0.0;
        double totaleSpese = 0.0;
        
        // Somma tutto
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totaleEntrate += transaction.getAmount();
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                totaleSpese += transaction.getAmount();
            }
        }
        
        double bilancio = totaleEntrate - totaleSpese;
        
        results.put("totaleEntrate", totaleEntrate);
        results.put("totaleSpese", totaleSpese);
        results.put("bilancio", bilancio);
        
        return results;
    }
    
    @Override
    public String getStrategyName() {
        return "Totali";
    }
    
    @Override
    public String getDescription() {
        return "Calcola totali entrate e spese";
    }
} 