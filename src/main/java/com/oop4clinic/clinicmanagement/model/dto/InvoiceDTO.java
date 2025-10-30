package com.oop4clinic.clinicmanagement.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDTO {
    private int id;
    private int appointmentId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private BigDecimal total;

    private List<InvoiceItemDTO> items;

    public InvoiceDTO() {}

    public InvoiceDTO(int id, int appointmentId, String status,
                      LocalDateTime createdAt, LocalDateTime paidAt,
                      BigDecimal subTotal, BigDecimal tax,
                      BigDecimal discount, BigDecimal total,
                      List<InvoiceItemDTO> items) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.total = total;
        this.items = items;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

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
                ", total=" + total +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", items=" + items +
                '}';
    }
}
