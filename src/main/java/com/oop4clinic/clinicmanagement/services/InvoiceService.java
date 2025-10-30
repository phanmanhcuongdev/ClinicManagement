package com.oop4clinic.clinicmanagement.services;

import com.oop4clinic.clinicmanagement.dao.InvoiceDAO;
import com.oop4clinic.clinicmanagement.model.entity.Invoice;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class InvoiceService {
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    public List<Invoice> getAll(){
        EntityManager em = EntityManagerProvider.em();
        try {
            return invoiceDAO.findAll(em);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public boolean updateInvoiceStatus(int id,InvoiceStatus newStatus){
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();

            Invoice invoice = em.find(Invoice.class, id);
            if (invoice != null) {
                invoice.setStatus(newStatus);
            }
            tx.commit();
            return true;
        }catch (Exception e){
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }finally {
            em.close();
        }
    }
}
