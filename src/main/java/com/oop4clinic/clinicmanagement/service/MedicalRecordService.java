package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.InvoiceDTO;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordDTO> getByPatientId(int patientId);

    List<MedicalRecordDTO> getAll();

    boolean updateMedicalRecord(MedicalRecordDTO record);

    MedicalRecordDTO getById(int id);

    MedicalRecordDTO findByAppointmentId(int appointmentId);
    MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto, int appointmentId, int doctorId);
    MedicalRecordDTO updateMedicalRecordWithProfession(MedicalRecordDTO dto);
    List<MedicalRecordDTO> getMedicalRecordsForPatient(int patientId);
    boolean deleteMedicalRecord(Integer id);
    List<MedicalRecordDTO> getMedicalRecordsForDoctor(int doctorId);
    List<MedicalRecordDTO> searchByPatientName(String keyword);
}

