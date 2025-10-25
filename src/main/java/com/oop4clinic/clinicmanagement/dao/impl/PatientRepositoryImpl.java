package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.PatientRepository;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {
    @Override
    public Patient create(EntityManager em,Patient patient)
    {
        if(patient.getId() == null)
        {
            em.persist(patient);
            return patient;
        }
        return em.merge(patient);
    }
    @Override
    public List<Patient> findAll(EntityManager em)
    {
        return em.createQuery(
                "select p from Patient p",Patient.class
        ).getResultList();
    }
    @Override
    public Patient update(EntityManager em,Patient patient)
    {
        if (patient.getId() == null) {
            throw new IllegalArgumentException("Không thể cập nhật bệnh nhân khi ID null.");
        }
        // merge sẽ trả về instance managed đã cập nhật
        return em.merge(patient);
    }
    @Override
    public long countAll(EntityManager em) {
        return em.createQuery(
                "select count(p) from Patient p",
                Long.class
        ).getSingleResult();
    }
    @Override
    public List<Patient> findNewest(EntityManager em, int limit) {
        // Nếu sau này bạn có createdAt thì ORDER BY createdAt desc.
        // Hiện tại ta sort theo id desc vì id tăng dần.
        return em.createQuery(
                "select p from Patient p order by p.id desc",
                Patient.class
        )
        .setMaxResults(limit)
        .getResultList();
    }
}
