package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordDTO> getAll();
    boolean updateMedicalRecord(MedicalRecordDTO record);
    List<MedicalRecordDTO> getMedicalRecordsForDoctor(int doctorId);
    List<MedicalRecordDTO> searchByPatientName(String keyword);
    boolean deleteMedicalRecord(Integer id);

    // Thêm các phương thức sau:
    MedicalRecordDTO findByAppointmentId(int appointmentId);
    MedicalRecordDTO createMedicalRecord(MedicalRecordDTO dto, int appointmentId, int doctorId);
    MedicalRecordDTO updateMedicalRecordWithProfession(MedicalRecordDTO dto);
    List<MedicalRecordDTO> getMedicalRecordsForPatient(int patientId);
}
