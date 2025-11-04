package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.service.query.PageRequest;
import com.oop4clinic.clinicmanagement.service.query.PageResult;
import com.oop4clinic.clinicmanagement.service.query.PatientFilter;

import java.util.List;

public interface PatientService {
    PatientDTO create(PatientDTO dto);
    List<PatientDTO> findAll();
    PatientDTO update(PatientDTO dto);
    List<PatientDTO> findByFilter(PatientFilter filter);
    PageResult<PatientDTO> findByFilter(PatientFilter filter, PageRequest page);
    // tbao
    PatientDTO findByPhone(String phone);
}
