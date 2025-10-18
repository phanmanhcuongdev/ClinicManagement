package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;

import java.util.*;

public interface DepartmentService {
    DepartmentDTO create(DepartmentDTO dto);
    List<DepartmentDTO> findAll();
}
