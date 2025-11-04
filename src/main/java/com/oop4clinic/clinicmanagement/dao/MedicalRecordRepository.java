package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import jakarta.persistence.EntityManager;

import java.util.List;

public interface MedicalRecordRepository {
    List<MedicalRecord> findAll(EntityManager em);
    boolean update(EntityManager em,MedicalRecord record);
    MedicalRecord findById(EntityManager em,int id);

    List<MedicalRecord> findByPatientId(EntityManager em, int patientId);

    List<MedicalRecord> searchByPatientName(String keyword);
    MedicalRecord findById(Integer id);
    MedicalRecord save(MedicalRecord record);

    boolean delete(Integer id);
}
