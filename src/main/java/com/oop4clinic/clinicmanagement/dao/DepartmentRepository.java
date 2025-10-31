package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Department;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {
    List<Department> findAll(EntityManager em);
    Optional<Department> findById(EntityManager em,Integer id);
    Department create(EntityManager em,Department dept);
    void deleteById(EntityManager em,Integer id);
    Department update(EntityManager em, Department dept);
}
