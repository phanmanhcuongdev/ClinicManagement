package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.InvoiceRepository;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.mapper.InvoiceMapper;
import com.oop4clinic.clinicmanagement.dao.impl.InvoiceRepositoryImpl;
import com.oop4clinic.clinicmanagement.model.dto.InvoiceDTO;
import com.oop4clinic.clinicmanagement.model.entity.Invoice;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;
import com.oop4clinic.clinicmanagement.service.InvoiceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {
    private InvoiceRepositoryImpl invoiceDAO = new InvoiceRepositoryImpl();
    private InvoiceMapper mapper = new InvoiceMapper();
    private final InvoiceRepository invoiceRepo = new InvoiceRepositoryImpl();


    @Override
    public List<InvoiceDTO> getAll(){
        EntityManager em = EntityManagerProvider.em();
        try {
            return mapper.toDtoList(invoiceDAO.findAll(em));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override // <--- Thêm triển khai này
    public List<InvoiceDTO> getInvoicesByPatientId(int patientId) {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<Invoice> invoices = invoiceDAO.findByPatientId(em, patientId);
            return mapper.toDtoList(invoices);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    @Override
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
