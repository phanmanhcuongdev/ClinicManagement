package com.oop4clinic.clinicmanagement.model.mapper;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordMapper {

    public MedicalRecordDTO toDto(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            return null;
        }

        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(medicalRecord.getId());
        dto.setSymptoms(medicalRecord.getSymptoms());
        dto.setDiagnosis(medicalRecord.getDiagnosis());
        dto.setPrescription(medicalRecord.getPrescription());
        dto.setNotes(medicalRecord.getNotes());
        dto.setCreatedAt(medicalRecord.getCreatedAt());

        if (medicalRecord.getAppointment() != null) {
            dto.setAppointmentId(medicalRecord.getAppointment().getId());
        }

        if (medicalRecord.getPatient() != null) {
            dto.setPatientId(medicalRecord.getPatient().getId());
            dto.setPatientName(medicalRecord.getPatient().getFullName());
        }

        if (medicalRecord.getDoctor() != null) {
            dto.setDoctorId(medicalRecord.getDoctor().getId());
            dto.setDoctorName(medicalRecord.getDoctor().getFullName());
        }

        return dto;
    }

    public List<MedicalRecordDTO> toDtoList(List<MedicalRecord> medicalRecords) {
        if (medicalRecords == null) {
            return null;
        }
        return medicalRecords.stream()
                           .map(this::toDto)
                           .collect(Collectors.toList());
    }

    public MedicalRecord toEntity(MedicalRecordDTO dto) {
        if (dto == null) {
            return null;
        }
        MedicalRecord entity = new MedicalRecord();
        entity.setSymptoms(dto.getSymptoms());
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setPrescription(dto.getPrescription());
        entity.setNotes(dto.getNotes());
        return entity;
    }

    public void updateEntityFromDto(MedicalRecordDTO dto, MedicalRecord entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setSymptoms(dto.getSymptoms());
        entity.setDiagnosis(dto.getDiagnosis());
        entity.setPrescription(dto.getPrescription());
        entity.setNotes(dto.getNotes());

    }
}
