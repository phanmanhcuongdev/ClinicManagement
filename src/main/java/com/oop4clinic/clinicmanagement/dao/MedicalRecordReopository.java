package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import jakarta.persistence.EntityManager;

import java.util.List;

public interface MedicalRecordReopository {
    List<MedicalRecord> findAll(EntityManager em);
    boolean update(EntityManager em,MedicalRecord record);
    MedicalRecord findById(EntityManager em,int id);
}
