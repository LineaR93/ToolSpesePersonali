package com.epicode;

import com.epicode.spesepersonali.exception.DataAccessException;
import com.epicode.spesepersonali.exception.ValidationException;
import com.epicode.spesepersonali.iterator.TransactionIterator;
import com.epicode.spesepersonali.model.Category;
import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.service.ExpenseService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * App per gestire le spese
 */
public class App {
    
    private static final Logger logger = Logger.getLogger(App.class.getName());
    
    private final ExpenseService expenseService;
    private final Scanner scanner;
    
    public App() throws DataAccessException {
        this.expenseService = new ExpenseService("data/transactions.csv");
        this.scanner = new Scanner(System.in);
        logger.info("Applicazione avviata");
    }
    
    public static void main(String[] args) {
        try {
            new App().start();
        } catch (DataAccessException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
    
    public void start() {
        System.out.println("=== GESTIONE SPESE ===");
        logger.info("Menu principale avviato");
        
        boolean running = true;
        while (running) {
            showMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        addExpense();
                        break;
                    case 2:
                        addIncome();
                        break;
                    case 3:
                        showTransactions();
                        break;
                    case 4:
                        showStats();
                        break;
                    case 5:
                        manageCategories();
                        break;
                    case 6:
                        saveData();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Ciao!");
                        logger.info("Applicazione chiusa");
                        break;
                    default:
                        System.out.println("Scelta non valida!");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Devi inserire un numero!");
            } catch (Exception e) {
                System.out.println("Errore: " + e.getMessage());
                logger.warning("Errore durante l'operazione: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    private void showMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Aggiungi spesa");
        System.out.println("2. Aggiungi entrata");
        System.out.println("3. Vedi transazioni");
        System.out.println("4. Statistiche");
        System.out.println("5. Gestisci categorie");
        System.out.println("6. Salva");
        System.out.println("0. Esci");
        System.out.print("Cosa vuoi fare? ");
    }
    
    private void addExpense() throws ValidationException, DataAccessException {
        System.out.print("Quanto hai speso? ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Per cosa? ");
        String description = scanner.nextLine();
        
        Category category = selectCategory();
        LocalDate date = getDate();
        
        expenseService.addExpense(amount, description, category, date);
        System.out.println("Spesa aggiunta!");
        logger.info("Spesa aggiunta: " + amount + "€ - " + description);
    }
    
    private void addIncome() throws ValidationException, DataAccessException {
        System.out.print("Quanto hai guadagnato? ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Da cosa? ");
        String description = scanner.nextLine();
        
        Category category = selectCategory();
        LocalDate date = getDate();
        
        expenseService.addIncome(amount, description, category, date);
        System.out.println("Entrata aggiunta!");
        logger.info("Entrata aggiunta: " + amount + "€ - " + description);
    }
    
    private Category selectCategory() {
        System.out.println("\nCategorie disponibili:");
        List<Category> categories = expenseService.getAllCategories();
        
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName());
        }
        
        System.out.print("Scegli categoria (numero): ");
        int choice = Integer.parseInt(scanner.nextLine()) - 1;
        
        if (choice >= 0 && choice < categories.size()) {
            return categories.get(choice);
        }
        
        return categories.get(0); // Default alla prima
    }
    
    private LocalDate getDate() {
        System.out.print("Data (YYYY-MM-DD) oppure invio per oggi: ");
        String dateInput = scanner.nextLine().trim();
        
        if (dateInput.isEmpty()) {
            return LocalDate.now();
        }
        
        try {
            return LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("Data non valida, uso oggi");
            return LocalDate.now();
        }
    }
    
    private void showTransactions() {
        TransactionIterator iterator = expenseService.getTransactionsByDate();
        
        if (iterator.getTotalCount() == 0) {
            System.out.println("Nessuna transazione");
            return;
        }
        
        System.out.println("\n--- TRANSAZIONI ---");
        logger.info("Visualizzazione transazioni: " + iterator.getTotalCount() + " trovate");
        
        while (iterator.hasNext()) {
            Transaction t = iterator.next();
            System.out.printf("%s | %s | %.2f€ | %s | %s%n",
                t.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                t.getType() == com.epicode.spesepersonali.model.TransactionType.EXPENSE ? "SPESA" : "ENTRATA",
                t.getAmount(),
                t.getCategory().getName(),
                t.getDescription()
            );
        }
    }
    
    private void showStats() {
        Map<String, Object> totals = expenseService.getTotals();
        Map<String, Object> categories = expenseService.getCategorySummary();
        
        System.out.println("\n--- STATISTICHE ---");
        System.out.printf("Totale entrate: %.2f€%n", (Double) totals.get("totaleEntrate"));
        System.out.printf("Totale spese: %.2f€%n", (Double) totals.get("totaleSpese"));
        System.out.printf("Bilancio: %.2f€%n", (Double) totals.get("bilancio"));
        
        logger.info("Statistiche calcolate - Bilancio: " + totals.get("bilancio") + "€");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> categorieSpese = (Map<String, Double>) categories.get("categorieSpese");
        
        if (!categorieSpese.isEmpty()) {
            System.out.println("\nSpese per categoria:");
            for (Map.Entry<String, Double> entry : categorieSpese.entrySet()) {
                System.out.printf("- %s: %.2f€%n", entry.getKey(), entry.getValue());
            }
        }
    }
    
    private void manageCategories() {
        System.out.println("\n--- CATEGORIE ---");
        System.out.println("1. Vedi categorie");
        System.out.println("2. Aggiungi categoria");
        System.out.print("Scegli: ");
        
        int choice = Integer.parseInt(scanner.nextLine());
        
        if (choice == 1) {
            showCategories();
        } else if (choice == 2) {
            addNewCategory();
        }
    }
    
    private void showCategories() {
        List<Category> categories = expenseService.getAllCategories();
        System.out.println("\nCategorie disponibili:");
        for (Category category : categories) {
            System.out.println("- " + category.getName() + ": " + category.getDescription());
        }
    }
    
    private void addNewCategory() {
        System.out.print("Nome categoria: ");
        String name = scanner.nextLine();
        
        System.out.print("Descrizione: ");
        String description = scanner.nextLine();
        
        expenseService.addCategory(name, description);
        System.out.println("Categoria aggiunta!");
        logger.info("Nuova categoria creata: " + name);
    }
    
    private void saveData() throws DataAccessException {
        expenseService.saveAll();
        System.out.println("Dati salvati!");
        logger.info("Dati salvati nel file CSV");
    }
}
