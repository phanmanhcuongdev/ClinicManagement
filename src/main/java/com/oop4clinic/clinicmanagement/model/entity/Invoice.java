package com.oop4clinic.clinicmanagement.model.entity;


import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "invoices",
    indexes = {
        @Index(name = "idx_invoice_appointment", columnList = "appointment_id"),
        @Index(name = "idx_invoice_status", columnList = "status")
    }
)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    public Invoice() {}

    public Invoice(Appointment appointment) {
        this.appointment = appointment;
    }

    // Helpers
    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        items.add(item);
    }
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }
    public void recomputeTotals() {
        subTotal = items.stream()
                .map(InvoiceItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        total = subTotal.add(tax).subtract(discount);
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (subTotal == null) subTotal = BigDecimal.ZERO;
        if (tax == null) tax = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        recomputeTotals();
    }

    @PreUpdate
    private void preUpdate() {
        recomputeTotals();
    }

    // Getters/Setters

    public long getId() { return id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) {
        this.status = status;
        if (status == InvoiceStatus.PAID && paidAt == null) {
            paidAt = LocalDateTime.now();
        }
        if (status != InvoiceStatus.PAID) {
            paidAt = null;
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<InvoiceItem> getItems() { return items; }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", appointmentId=" + (appointment != null ? appointment.getId() : null) +
                ", status=" + status +
                ", subTotal=" + subTotal +
                ", tax=" + tax +
                ", discount=" + discount +
                ", total=" + total +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                '}';
    }
}