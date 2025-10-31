package com.oop4clinic.clinicmanagement.dao.impl;

import com.oop4clinic.clinicmanagement.dao.DepartmentRepository;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class DepartmentRepositoryImpl implements DepartmentRepository {
    @Override
    public List<Department> findAll(EntityManager em)
    {
        return em.createQuery(
                "select d from Department d order by  d.id",
                Department.class
        ).getResultList();
    }
    @Override
    public Optional<Department> findById(EntityManager em,Integer id)
    {
        return Optional.ofNullable(em.find(Department.class,id));
    }
    @Override
    public Department create(EntityManager em,Department dept)
    {
        if(dept.getId() == null)
        {
            em.persist(dept);
            return dept;
        }
        return em.merge(dept);
    }

    @Override
    public void deleteById(EntityManager em,Integer id)
    {
        Department found = em.find(Department.class,id);
        if(found!=null)
        {
            em.remove(found);
        }
    }

    @Override
    public Department update(EntityManager em, Department dept) {
        if (dept.getId() == null) {
            throw new IllegalArgumentException("Department ID cannot be null for update");
        }

        Department existing = em.find(Department.class, dept.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Department not found with ID: " + dept.getId());
        }

        existing.setName(dept.getName());
        existing.setBaseFee(dept.getBaseFee());
        existing.setDescription(dept.getDescription());
        // không set doctors/appointments ở đây để tránh cascade không mong muốn

        return em.merge(existing);
    }

}
