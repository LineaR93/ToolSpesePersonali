package com.epicode;

import static org.junit.Assert.*;

import com.epicode.spesepersonali.exception.ValidationException;
import com.epicode.spesepersonali.factory.TransactionFactory;
import com.epicode.spesepersonali.iterator.TransactionByDateIterator;
import com.epicode.spesepersonali.model.Category;
import com.epicode.spesepersonali.model.Transaction;
import com.epicode.spesepersonali.model.TransactionType;
import com.epicode.spesepersonali.strategy.MonthlyTotalStrategy;
import com.epicode.spesepersonali.strategy.CategorySummaryStrategy;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Test semplici
 */
public class AppTest {
    
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }
    
    // Test Factory - Crea spesa
    @Test
    public void testFactoryCreateExpense() throws ValidationException {
        Category category = new Category("Cibo", "Spese per mangiare");
        
        Transaction expense = TransactionFactory.createExpense(50.0, "Spesa supermercato", category);
        
        assertNotNull(expense);
        assertEquals(TransactionType.EXPENSE, expense.getType());
        assertEquals(50.0, expense.getAmount(), 0.01);
        assertEquals("Spesa supermercato", expense.getDescription());
        assertEquals("Cibo", expense.getCategory().getName());
        assertNotNull(expense.getId());
    }
    
    @Test
    public void testFactoryCreateIncome() throws ValidationException {
        Category category = new Category("Stipendio", "Soldi del lavoro");
        
        Transaction income = TransactionFactory.createIncome(1500.0, "Stipendio mensile", category);
        
        assertNotNull(income);
        assertEquals(TransactionType.INCOME, income.getType());
        assertEquals(1500.0, income.getAmount(), 0.01);
        assertEquals("Stipendio mensile", income.getDescription());
    }
    
    // Test Factory - Errori
    @Test(expected = ValidationException.class)
    public void testFactoryValidationNegativeAmount() throws ValidationException {
        Category category = new Category("Test", "Test");
        TransactionFactory.createExpense(-10.0, "Invalid", category);
    }
    
    @Test(expected = ValidationException.class)
    public void testFactoryValidationEmptyDescription() throws ValidationException {
        Category category = new Category("Test", "Test");
        TransactionFactory.createExpense(10.0, "", category);
    }
    
    @Test(expected = ValidationException.class)
    public void testFactoryValidationNullCategory() throws ValidationException {
        TransactionFactory.createExpense(10.0, "Test", null);
    }
    
    // Test Iterator
    @Test
    public void testIteratorByDate() throws ValidationException {
        Category category = new Category("Test", "Test category");
        
        List<Transaction> transactions = Arrays.asList(
            TransactionFactory.createExpense(100.0, "Older expense", category, LocalDate.of(2024, 1, 1)),
            TransactionFactory.createExpense(200.0, "Newer expense", category, LocalDate.of(2024, 2, 1)),
            TransactionFactory.createIncome(300.0, "Recent income", category, LocalDate.of(2024, 3, 1))
        );
        
        TransactionByDateIterator iterator = new TransactionByDateIterator(transactions);
        
        assertEquals(3, iterator.getTotalCount());
        assertEquals(0, iterator.getCurrentPosition());
        
        assertTrue(iterator.hasNext());
        Transaction first = iterator.next();
        assertEquals(LocalDate.of(2024, 3, 1), first.getDate()); // Più recente
        
        assertTrue(iterator.hasNext());
        Transaction second = iterator.next();
        assertEquals(LocalDate.of(2024, 2, 1), second.getDate());
        
        assertTrue(iterator.hasNext());
        Transaction third = iterator.next();
        assertEquals(LocalDate.of(2024, 1, 1), third.getDate()); // Più vecchia
        
        assertFalse(iterator.hasNext());
        assertEquals(3, iterator.getCurrentPosition());
        
        // Test reset
        iterator.reset();
        assertEquals(0, iterator.getCurrentPosition());
        assertTrue(iterator.hasNext());
    }
    
    // Test Strategy - Totali
    @Test
    public void testStrategyTotals() throws ValidationException {
        Category foodCategory = new Category("Cibo", "Spese cibo");
        Category salaryCategory = new Category("Stipendio", "Entrate lavoro");
        
        List<Transaction> transactions = Arrays.asList(
            TransactionFactory.createExpense(100.0, "Spesa gennaio", foodCategory),
            TransactionFactory.createExpense(150.0, "Spesa febbraio", foodCategory),
            TransactionFactory.createIncome(1000.0, "Stipendio gennaio", salaryCategory),
            TransactionFactory.createIncome(1000.0, "Stipendio febbraio", salaryCategory)
        );
        
        MonthlyTotalStrategy strategy = new MonthlyTotalStrategy();
        Map<String, Object> results = strategy.calculate(transactions);
        
        assertNotNull(results);
        assertEquals("Totali", strategy.getStrategyName());
        
        // Verifica i totali
        assertEquals(2000.0, (Double) results.get("totaleEntrate"), 0.01);
        assertEquals(250.0, (Double) results.get("totaleSpese"), 0.01);
        assertEquals(1750.0, (Double) results.get("bilancio"), 0.01);
    }
    
    // Test Strategy - Categorie
    @Test
    public void testStrategyCategories() throws ValidationException {
        Category foodCategory = new Category("Cibo", "Spese cibo");
        Category transportCategory = new Category("Trasporti", "Spese trasporti");
        Category salaryCategory = new Category("Stipendio", "Entrate lavoro");
        
        List<Transaction> transactions = Arrays.asList(
            TransactionFactory.createExpense(100.0, "Spesa supermercato", foodCategory),
            TransactionFactory.createExpense(50.0, "Altra spesa cibo", foodCategory),
            TransactionFactory.createExpense(30.0, "Benzina", transportCategory),
            TransactionFactory.createIncome(1000.0, "Stipendio", salaryCategory) // Non conta nelle spese
        );
        
        CategorySummaryStrategy strategy = new CategorySummaryStrategy();
        Map<String, Object> results = strategy.calculate(transactions);
        
        assertNotNull(results);
        assertEquals("Categorie", strategy.getStrategyName());
        
        @SuppressWarnings("unchecked")
        Map<String, Double> categorieSpese = (Map<String, Double>) results.get("categorieSpese");
        
        assertNotNull(categorieSpese);
        assertEquals(150.0, categorieSpese.get("Cibo"), 0.01);
        assertEquals(30.0, categorieSpese.get("Trasporti"), 0.01);
        assertFalse(categorieSpese.containsKey("Stipendio")); // Non è una spesa
    }
    
    // Test Category
    @Test
    public void testCategory() {
        Category category = new Category("Cibo", "Spese per mangiare");
        
        assertEquals("Cibo", category.getName());
        assertEquals("Spese per mangiare", category.getDescription());
        assertEquals(0.0, category.getTotalAmount(), 0.01);
        
        // Test setTotalAmount
        category.setTotalAmount(100.0);
        assertEquals(100.0, category.getTotalAmount(), 0.01);
    }
    
    // Test Eccezioni
    @Test
    public void testExceptionHandling() {
        try {
            Category category = new Category("Test", "Test");
            TransactionFactory.createExpense(0.0, "Zero amount", category);
            fail("Doveva dare errore per importo zero");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("positivo"));
        }
    }
    
    // Test equals Transaction
    @Test
    public void testTransactionEquality() throws ValidationException {
        Category category = new Category("Test", "Test");
        Transaction t1 = TransactionFactory.createExpense(100.0, "Test", category);
        Transaction t2 = TransactionFactory.createExpense(200.0, "Different", category);
        
        // Diverse se hanno ID diversi
        assertEquals(t1, t1);
        assertNotEquals(t1, t2);
        assertNotNull(t1.hashCode());
        assertNotNull(t1.toString());
    }
    
    // Test equals Category
    @Test
    public void testCategoryEquality() {
        Category c1 = new Category("Cibo", "Spese cibo");
        Category c2 = new Category("Cibo", "Descrizione diversa");
        Category c3 = new Category("Trasporti", "Spese trasporti");
        
        // Uguali se stesso nome
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotNull(c1.toString());
    }
}
