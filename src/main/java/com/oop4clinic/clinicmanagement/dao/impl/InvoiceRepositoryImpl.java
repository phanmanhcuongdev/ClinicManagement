package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.InvoiceRepository;
import com.oop4clinic.clinicmanagement.model.entity.Invoice;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class InvoiceRepositoryImpl implements InvoiceRepository {

    @Override
    public Invoice save(EntityManager em, Invoice invoice) {
        if (invoice.getId() == null) {
            em.persist(invoice);
            return invoice;
        }
        return em.merge(invoice);
    }

    @Override
    public List<Invoice> findByCreatedAtBetween(EntityManager em,
                                                LocalDateTime from,
                                                LocalDateTime to) {

        return em.createQuery(
                """
                select i
                from Invoice i
                join fetch i.patient p
                join fetch i.appointment a
                where i.createdAt between :from and :to
                order by i.createdAt asc
                """,
                Invoice.class
        )
        .setParameter("from", from)
        .setParameter("to",   to)
        .getResultList();
    }

    @Override
    public double sumByCreatedAtBetween(EntityManager em,
                                        LocalDateTime from,
                                        LocalDateTime to) {

        Double sum = em.createQuery(
                """
                select sum(i.total)
                from Invoice i
                where i.createdAt between :from and :to
                """,
                Double.class
        )
        .setParameter("from", from)
        .setParameter("to",   to)
        .getSingleResult();

        return (sum == null ? 0.0 : sum);
    }
}
