package com.oop4clinic.clinicmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDTO {
    private long id;
    private long appointmentId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private BigDecimal subTotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;

    private List<InvoiceItemDTO> items;

    public InvoiceDTO() {}

    public InvoiceDTO(long id, long appointmentId, String status,
                      LocalDateTime createdAt, LocalDateTime paidAt,
                      BigDecimal subTotal, BigDecimal tax,
                      BigDecimal discount, BigDecimal total,
                      List<InvoiceItemDTO> items) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.subTotal = subTotal;
        this.tax = tax;
        this.discount = discount;
        this.total = total;
        this.items = items;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(long appointmentId) { this.appointmentId = appointmentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }

    @Override
    public String toString() {
        return "InvoiceDTO{" +
                "id=" + id +
                ", appointmentId=" + appointmentId +
                ", status='" + status + '\'' +
                ", subTotal=" + subTotal +
                ", tax=" + tax +
                ", discount=" + discount +
                ", total=" + total +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", items=" + items +
                '}';
    }
}
