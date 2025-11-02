package com.oop4clinic.clinicmanagement.model.dto;

import java.math.BigDecimal;

public class InvoiceItemDto {
    private int id;
    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public InvoiceItemDto() {}

    public InvoiceItemDto(int id, String description, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
