package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Patient;
import jakarta.persistence.EntityManager;

import java.util.List;

public interface PatientRepository {
    Patient create(EntityManager em, Patient e);
    List<Patient> findAll(EntityManager em);
    Patient update(EntityManager em,Patient e);
    long countAll(EntityManager em);
    List<Patient> findNewest(EntityManager em, int limit);
}
