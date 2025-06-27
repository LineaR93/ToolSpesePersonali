package com.epicode.spesepersonali.model;

/**
 * Una categoria per le spese
 */
public class Category {
    
    private final String name;
    private final String description;
    private double totalAmount;
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.totalAmount = 0.0;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Category category = (Category) obj;
        return name.equals(category.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Category{name='%s', description='%s'}", name, description);
    }
} 