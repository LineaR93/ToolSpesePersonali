package com.epicode.spesepersonali.strategy;

import com.epicode.spesepersonali.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Interfaccia per le diverse strategie di calcolo
 */
public interface CalculationStrategy {
    
    /**
     * Esegue il calcolo sui dati forniti
     * @param transactions Lista delle transazioni da analizzare
     * @return Mappa con i risultati del calcolo
     */
    Map<String, Object> calculate(List<Transaction> transactions);
    
    /**
     * Nome identificativo della strategia
     */
    String getStrategyName();
    
    /**
     * Descrizione di cosa fa questa strategia
     */
    String getDescription();
} 