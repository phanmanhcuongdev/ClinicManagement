package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.MedicalRecordReopository;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
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

    @Override
    public MedicalRecord save(MedicalRecord record) {
        EntityManager em = EntityManagerProvider.em();
        try {
            em.getTransaction().begin();

            if (record.getId() == null || em.find(MedicalRecord.class, record.getId()) == null) {
                em.persist(record); // tạo mới
            } else {
                record = em.merge(record); // cập nhật
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
}
