package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordDTO> getByPatientId(int patientId);

    List<MedicalRecordDTO> getAll();

    boolean updateMedicalRecord(MedicalRecordDTO record);
    MedicalRecordDTO getById(int id);

}
