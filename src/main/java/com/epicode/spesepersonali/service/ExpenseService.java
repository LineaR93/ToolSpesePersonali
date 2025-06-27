package com.epicode.spesepersonali.service;

import com.epicode.spesepersonali.exception.DataAccessException;
import com.epicode.spesepersonali.exception.ValidationException;
import com.epicode.spesepersonali.factory.TransactionFactory;
import com.epicode.spesepersonali.iterator.TransactionByDateIterator;
import com.epicode.spesepersonali.iterator.TransactionIterator;
import com.epicode.spesepersonali.model.Category;
import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.repository.TransactionRepository;
import com.epicode.spesepersonali.strategy.CalculationStrategy;
import com.epicode.spesepersonali.strategy.CategorySummaryStrategy;
import com.epicode.spesepersonali.strategy.MonthlyTotalStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Gestisce le operazioni principali
 */
public class ExpenseService {
    
    private static final Logger logger = Logger.getLogger(ExpenseService.class.getName());
    
    private final TransactionRepository repository;
    private final List<Transaction> transactions;
    private final Map<String, Category> categories;
    
    public ExpenseService(String dataFilePath) throws DataAccessException {
        this.repository = new TransactionRepository(dataFilePath);
        this.transactions = new ArrayList<>();
        this.categories = new HashMap<>();
        
        // Crea categorie base
        initializeDefaultCategories();
        loadExistingTransactions();
        
        logger.info("ExpenseService inizializzato con " + transactions.size() + " transazioni");
    }
    
    // Categorie di base
    private void initializeDefaultCategories() {
        addCategory("Lavoro", "Entrate da lavoro e freelance");
        addCategory("Mutuo/Affitto", "Spese per casa");
        addCategory("Utenze", "Bollette varie");
        addCategory("Cibo", "Spese per mangiare");
        addCategory("Intrattenimento", "Divertimento e hobby");
        addCategory("Stipendio", "Stipendio mensile");
        addCategory("Trasporti", "Auto, benzina, mezzi pubblici");
        addCategory("Altro", "Altre spese");
        
        logger.info("Categorie predefinite inizializzate: " + categories.size());
    }
    
    // Aggiunge spesa
    public void addExpense(double amount, String description, Category category, LocalDate date) 
            throws ValidationException, DataAccessException {
        
        Transaction expense = TransactionFactory.createExpense(amount, description, category, date);
        transactions.add(expense);
        repository.appendTransaction(expense);
        
        logger.info("Spesa registrata: " + amount + "€ in categoria " + category.getName());
    }
    
    // Aggiunge entrata
    public void addIncome(double amount, String description, Category category, LocalDate date) 
            throws ValidationException, DataAccessException {
        
        Transaction income = TransactionFactory.createIncome(amount, description, category, date);
        transactions.add(income);
        repository.appendTransaction(income);
        
        logger.info("Entrata registrata: " + amount + "€ in categoria " + category.getName());
    }
    
    // Lista transazioni
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }
    
    // Ottieni iteratore
    public TransactionIterator getTransactionsByDate() {
        return new TransactionByDateIterator(transactions);
    }
    
    // Statistiche semplici
    public Map<String, Object> getTotals() {
        CalculationStrategy strategy = new MonthlyTotalStrategy();
        Map<String, Object> results = strategy.calculate(transactions);
        
        logger.info("Calcolo totali completato");
        return results;
    }
    
    // Spese per categoria
    public Map<String, Object> getCategorySummary() {
        CalculationStrategy strategy = new CategorySummaryStrategy();
        Map<String, Object> results = strategy.calculate(transactions);
        
        logger.info("Calcolo riepilogo categorie completato");
        return results;
    }
    
    // Gestione categorie
    public void addCategory(String name, String description) {
        categories.put(name, new Category(name, description));
    }
    
    public Category getCategory(String name) {
        return categories.get(name);
    }
    
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories.values());
    }
    
    // Salva tutto
    public void saveAll() throws DataAccessException {
        repository.saveTransactions(transactions);
        logger.info("Tutte le transazioni salvate su file CSV");
    }
    
    // Carica dal file
    private void loadExistingTransactions() throws DataAccessException {
        try {
            List<Transaction> loaded = repository.loadTransactions();
            transactions.addAll(loaded);
            
            if (!loaded.isEmpty()) {
                logger.info("Caricate " + loaded.size() + " transazioni esistenti dal file");
            }
        } catch (DataAccessException e) {
            logger.warning("Impossibile caricare transazioni esistenti: " + e.getMessage());
            // Se non riesce a caricare, parte vuoto
        }
    }
} 