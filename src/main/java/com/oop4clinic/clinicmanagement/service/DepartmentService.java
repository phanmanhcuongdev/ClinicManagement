package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;

import java.util.*;

public interface DepartmentService {
    DepartmentDTO create(DepartmentDTO dto);
    List<DepartmentDTO> findAll();
    void deleteById(Integer id);
    DepartmentDTO update(Integer id,DepartmentDTO dto);
}
