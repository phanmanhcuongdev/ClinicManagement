package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Invoice;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository {

    List<Invoice>  findAll(EntityManager em);
    Invoice save(EntityManager em, Invoice invoice);

    /**
     * Lấy tất cả invoice trong khoảng thời gian [from, to],
     * sort theo createdAt tăng dần.
     */
    List<Invoice> findByCreatedAtBetween(EntityManager em,
                                         LocalDateTime from,
                                         LocalDateTime to);

    /**
     * Tính tổng doanh thu (sum of total) trong khoảng thời gian [from, to].
     * Dùng cho KPI doanh thu hôm nay / tuần này / tháng này.
     */
    double sumByCreatedAtBetween(EntityManager em,
                                 LocalDateTime from,
                                 LocalDateTime to);
}
