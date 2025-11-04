package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.MedicalRecordRepository;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MedicalRecordImpl implements MedicalRecordRepository {

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
    public List<MedicalRecord> searchByPatientName(String keyword) {
        EntityManager em = EntityManagerProvider.em();
        try {

            TypedQuery<MedicalRecord> query = em.createQuery(
                    "SELECT m FROM MedicalRecord m JOIN FETCH m.patient p WHERE LOWER(p.fullName) LIKE LOWER(:kw)", // Sửa ở đây
                    MedicalRecord.class
            );
            query.setParameter("kw", "%" + keyword + "%");
            return query.getResultList();
        } finally {

            em.close();
        }
    }
    @Override
    public MedicalRecord findById(Integer id) {
        EntityManager em = EntityManagerProvider.em();
        try {
            return em.find(MedicalRecord.class, id);
        } finally {
            em.close();
        }
    }
    @Override
    public MedicalRecord save(MedicalRecord record) {
        EntityManager em = EntityManagerProvider.em();
        try {
            em.getTransaction().begin();

            if (record.getId() == null || em.find(MedicalRecord.class, record.getId()) == null) {
                em.persist(record);
            } else {
                record = em.merge(record);
            }

            em.getTransaction().commit();
            return record;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
    @Override
    public boolean delete(Integer id) {
        EntityManager em = EntityManagerProvider.em();
        try {
            em.getTransaction().begin();
            MedicalRecord record = em.find(MedicalRecord.class, id);
            if (record != null) {
                em.remove(record);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
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