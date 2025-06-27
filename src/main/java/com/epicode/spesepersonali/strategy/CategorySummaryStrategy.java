package com.epicode.spesepersonali.strategy;

import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.model.TransactionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Raggruppa per categoria
 */
public class CategorySummaryStrategy implements CalculationStrategy {
    
    @Override
    public Map<String, Object> calculate(List<Transaction> transactions) {
        Map<String, Object> results = new HashMap<>();
        Map<String, Double> categorieSpese = new HashMap<>();
        
        // Conta per categoria
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                String categoria = transaction.getCategory().getName();
                double importo = categorieSpese.getOrDefault(categoria, 0.0);
                categorieSpese.put(categoria, importo + transaction.getAmount());
            }
        }
        
        results.put("categorieSpese", categorieSpese);
        
        return results;
    }
    
    @Override
    public String getStrategyName() {
        return "Categorie";
    }
    
    @Override
    public String getDescription() {
        return "Raggruppa spese per categoria";
    }
} 