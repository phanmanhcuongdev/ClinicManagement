package com.oop4clinic.clinicmanagement.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Invoice {
    private final SimpleIntegerProperty orderNumber;

    private final SimpleStringProperty invoiceId;

    private final SimpleStringProperty description;

    private final SimpleStringProperty status;

    private final SimpleDoubleProperty amountDue;

    private final SimpleDoubleProperty amountPaid;

    public Invoice(int orderNumber, String invoiceId, String description, String status, double amountDue, double amountPaid) {
        this.orderNumber = new SimpleIntegerProperty(orderNumber);
        this.invoiceId = new SimpleStringProperty(invoiceId);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
        this.amountDue = new SimpleDoubleProperty(amountDue);
        this.amountPaid = new SimpleDoubleProperty(amountPaid);
    }

    public int getOrderNumber() { return orderNumber.get(); }
    public String getInvoiceId() { return invoiceId.get(); }
    public String getDescription() { return description.get(); }
    public String getStatus() { return status.get(); }
    public double getAmountDue() { return amountDue.get(); }
    public double getAmountPaid() { return amountPaid.get(); }

}