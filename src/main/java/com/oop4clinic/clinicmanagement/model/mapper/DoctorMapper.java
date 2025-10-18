package com.oop4clinic.clinicmanagement.model.mapper;

import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;

public final class DoctorMapper {
    private DoctorMapper() {}

    public static DoctorDTO toDTO(Doctor e) {
        if (e == null) return null;
        DoctorDTO dto = new DoctorDTO();
        dto.setId(e.getId());
        if (e.getDepartment() != null) {
            dto.setDepartmentId(e.getDepartment().getId());
            dto.setDepartmentName(e.getDepartment().getName());
        }
        dto.setFullName(e.getFullName());
        dto.setGender(e.getGender());
        dto.setDateOfBirth(e.getDateOfBirth());
        dto.setPhone(e.getPhone());
        dto.setEmail(e.getEmail());
        dto.setAddress(e.getAddress());
        dto.setConsultationFee(e.getConsultationFee());
        dto.setDoctorStatus(e.getStatus());
        return dto;
    }

    public static Doctor toEntityForCreate(DoctorDTO dto, Department managedDept) {
        Doctor e = new Doctor();
        apply(dto, e, managedDept);
        return e;
    }

    public static void applyForUpdate(DoctorDTO dto, Doctor target, Department managedDept) {
        apply(dto, target, managedDept);
    }

    private static void apply(DoctorDTO dto, Doctor e, Department dept) {
        e.setFullName(dto.getFullName());
        e.setGender(dto.getGender());
        e.setDateOfBirth(dto.getDateOfBirth());
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        e.setAddress(dto.getAddress());
        e.setConsultationFee(dto.getConsultationFee());
        e.setStatus(dto.getDoctorStatus());
        if (dept != null) e.setDepartment(dept);
    }
}

