package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.MedicalRecordReopository;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class MedicalRecordImpl implements MedicalRecordReopository {
    @Override
    public List<MedicalRecord> findAll(EntityManager em){
        String jpql = """
                Select m from MedicalRecord m 
                JOIN FETCH m.doctor 
                JOIN FETCH m.patient
                """;
        return em.createQuery(jpql,MedicalRecord.class).getResultList();
    }

    @Override
    public boolean update(EntityManager em,MedicalRecord record) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(record);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MedicalRecord findById(EntityManager em, int id){
        return em.find(MedicalRecord.class,id);
    }
}
