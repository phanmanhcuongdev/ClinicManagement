package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.MedicalRecordReopository;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class MedicalRecordImpl implements MedicalRecordReopository {

    // BỔ SUNG PHƯƠNG THỨC NÀY ĐỂ TRIỂN KHAI HOÀN CHỈNH INTERFACE
    @Override
    public List<MedicalRecord> findAll(EntityManager em){
        String jpql = """
                Select m from MedicalRecord m 
                JOIN FETCH m.doctor 
                JOIN FETCH m.patient
                """;
        return em.createQuery(jpql,MedicalRecord.class).getResultList();
    }

    // Phương thức đã được thêm vào trước đó để lọc theo người dùng đăng nhập
    @Override
    public List<MedicalRecord> findByPatientId(EntityManager em, int patientId) {
        String jpql = """
                SELECT m FROM MedicalRecord m 
                JOIN FETCH m.doctor 
                JOIN FETCH m.patient
                WHERE m.patient.id = :patientId
                """;
        return em.createQuery(jpql, MedicalRecord.class)
                .setParameter("patientId", patientId)
                .getResultList();
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