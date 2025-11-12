package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.PatientRepository;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class PatientRepositoryImpl implements PatientRepository {

    @Override
    // tim kiem theo id benh nhan
    public Optional<Patient> findById(EntityManager em, int id) {
        try {
            Patient p = em.find(Patient.class, id);
            return Optional.ofNullable(p);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Patient create(EntityManager em, Patient p) {
        if (p.getId() == null) {
            em.persist(p);
            return p;
        }
        return em.merge(p);
    }

    @Override
    public List<Patient> findAll(EntityManager em) {
        return em.createQuery("SELECT p FROM Patient p", Patient.class)
                .getResultList();
    }

    @Override
    public Patient update(EntityManager em, Patient p) {
        if (p.getId() == null) {
            throw new IllegalArgumentException("Không thể cập nhật bệnh nhân khi ID null.");
        }
        return em.merge(p);
    }

    @Override
    public long countAll(EntityManager em) {
        return em.createQuery("SELECT COUNT(p) FROM Patient p", Long.class)
                .getSingleResult();
    }

    @Override
    public List<Patient> findNewest(EntityManager em, int limit) {
        return em.createQuery("SELECT p FROM Patient p ORDER BY p.id DESC", Patient.class)
                .setMaxResults(limit)
                .getResultList();
    }

    // tim benh nhan theo so dien thoai
    @Override
    public Optional<Patient> findByPhone(EntityManager em, String phone) {
        try {
            Patient p = em.createQuery(
                            "SELECT p FROM Patient p WHERE p.phone = :phone", Patient.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
            return Optional.of(p);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
