package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import jakarta.persistence.EntityManager;

import java.util.*;

public interface DoctorService {
    DoctorDTO create(DoctorDTO dto);
    List<DoctorDTO> findAll();
    DoctorDTO findById(int id);
    DoctorDTO update(DoctorDTO dto);
    List<DoctorDTO> searchDoctors(String keyword, Integer departmentId, DoctorStatus status);
    DoctorDTO findByPhone(String phonenumber);
}
