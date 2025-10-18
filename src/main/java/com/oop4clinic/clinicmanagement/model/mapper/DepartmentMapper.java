package com.oop4clinic.clinicmanagement.model.mapper;

import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;

public class DepartmentMapper {
     private DepartmentMapper() {} // không cho new

    public static DepartmentDTO toDTO(Department e) {
        if (e == null) return null;
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setBaseFee(e.getBaseFee());
        dto.setDescription(e.getDescription());
        return dto;
    }

    public static Department toEntity(DepartmentDTO dto) {
        Department e = new Department();
        e.setName(dto.getName());
        e.setBaseFee(dto.getBaseFee());
        e.setDescription(dto.getDescription());
        return e;
    }
}
