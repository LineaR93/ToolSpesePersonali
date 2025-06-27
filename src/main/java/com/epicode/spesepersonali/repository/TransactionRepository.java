package com.epicode.spesepersonali.repository;

import com.epicode.spesepersonali.exception.DataAccessException;
import com.epicode.spesepersonali.model.Category;
import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.model.TransactionType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Gestisce i file CSV
 */
public class TransactionRepository {
    
    private static final Logger logger = Logger.getLogger(TransactionRepository.class.getName());
    private static final String CSV_HEADER = "ID,Tipo,Importo,Descrizione,Categoria,Data";
    private static final String CSV_SEPARATOR = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private final String filePath;
    
    public TransactionRepository(String filePath) {
        this.filePath = filePath;
        logger.info("Repository inizializzato per file: " + filePath);
    }
    
    // Salva tutto
    public void saveTransactions(List<Transaction> transactions) throws DataAccessException {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            
            writer.println(CSV_HEADER);
            
            for (Transaction transaction : transactions) {
                writer.println(formatTransactionToCSV(transaction));
            }
            
            logger.info("Salvate " + transactions.size() + " transazioni nel file CSV");
            
        } catch (IOException e) {
            logger.severe("Errore nel salvare le transazioni: " + e.getMessage());
            throw new DataAccessException("Errore nel salvare le transazioni");
        }
    }
    
    // Carica tutto
    public List<Transaction> loadTransactions() throws DataAccessException {
        List<Transaction> transactions = new ArrayList<>();
        
        File file = new File(filePath);
        if (!file.exists()) {
            logger.info("File CSV non trovato, verrà creato al primo salvataggio");
            return transactions;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = reader.readLine(); // Salta header
            if (line == null) {
                return transactions;
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Transaction transaction = parseTransactionFromCSV(line);
                    transactions.add(transaction);
                } catch (Exception e) {
                    logger.warning("Riga CSV ignorata (formato non valido): " + line);
                }
            }
            
            logger.info("Caricate " + transactions.size() + " transazioni dal file CSV");
            
        } catch (IOException e) {
            logger.severe("Errore nel caricare le transazioni: " + e.getMessage());
            throw new DataAccessException("Errore nel caricare le transazioni");
        }
        
        return transactions;
    }
    
    // Aggiunge una transazione al file CSV
    public void appendTransaction(Transaction transaction) throws DataAccessException {
        try {
            boolean fileExists = new File(filePath).exists();
            
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(filePath, true), StandardCharsets.UTF_8))) {
                
                if (!fileExists) {
                    writer.println(CSV_HEADER);
                    logger.info("Creato nuovo file CSV con header");
                }
                
                writer.println(formatTransactionToCSV(transaction));
                logger.info("Transazione aggiunta al file CSV: " + transaction.getId());
            }
            
        } catch (IOException e) {
            logger.severe("Errore nell'aggiungere la transazione: " + e.getMessage());
            throw new DataAccessException("Errore nell'aggiungere la transazione");
        }
    }
    
    private String formatTransactionToCSV(Transaction transaction) {
        return String.join(CSV_SEPARATOR,
                escapeCSV(transaction.getId()),
                escapeCSV(transaction.getType().name()),
                String.valueOf(transaction.getAmount()),
                escapeCSV(transaction.getDescription()),
                escapeCSV(transaction.getCategory().getName()),
                transaction.getDate().format(DATE_FORMATTER)
        );
    }
    
    private Transaction parseTransactionFromCSV(String csvLine) throws DataAccessException {
        String[] fields = csvLine.split(CSV_SEPARATOR);
        
        if (fields.length != 6) {
            throw new DataAccessException("Formato CSV non valido");
        }
        
        try {
            TransactionType type = TransactionType.valueOf(unescapeCSV(fields[1]));
            double amount = Double.parseDouble(fields[2]);
            String description = unescapeCSV(fields[3]);
            String categoryName = unescapeCSV(fields[4]);
            LocalDate date = LocalDate.parse(fields[5], DATE_FORMATTER);
            
            Category category = new Category(categoryName, "Categoria dal CSV");
            
            return new Transaction(type, amount, description, category, date);
            
        } catch (NumberFormatException e) {
            throw new DataAccessException("Numero non valido nel CSV");
        } catch (DateTimeParseException e) {
            throw new DataAccessException("Data non valida nel CSV");
        } catch (IllegalArgumentException e) {
            throw new DataAccessException("Tipo transazione non valido nel CSV");
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            String escaped = value.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return value;
    }
    
    private String unescapeCSV(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String content = value.substring(1, value.length() - 1);
            return content.replace("\"\"", "\"");
        }
        
        return value;
    }
} 