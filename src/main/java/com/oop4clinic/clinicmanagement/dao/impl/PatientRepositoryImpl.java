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
}
