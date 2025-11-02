package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import jakarta.persistence.EntityManager;

import java.util.List;

public interface MedicalRecordReopository {
    List<MedicalRecord> findAll(EntityManager em);
    boolean update(EntityManager em,MedicalRecord record);
    List<MedicalRecord> searchByPatientName(String keyword);
    MedicalRecord findById(Integer id);
    MedicalRecord save(MedicalRecord record);

    boolean delete(Integer id);
}
