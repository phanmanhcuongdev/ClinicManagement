package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DepartmentRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DepartmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.mapper.DepartmentMapper;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.*;
import java.util.stream.Collectors;

public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository deptRepo = new DepartmentRepositoryImpl();

    @Override
    public List<DepartmentDTO> findAll()
    {
        EntityManager em = EntityManagerProvider.em();
        try {
            var list = deptRepo.findAll(em);

            return DepartmentMapper.toDtoList(list);
        } finally {
            em.close();
        }
    }

    @Override
    public DepartmentDTO create(DepartmentDTO dto)
    {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();

        try
        {
            tx.begin();

            if(dto == null) throw new IllegalArgumentException("Chuyên ngành rỗng");

            if(dto.getName() == null || dto.getName().isBlank())
                throw new IllegalArgumentException("Tên ngành rỗng");

            Department entity = DepartmentMapper.toEntity(dto);

            deptRepo.create(em,entity);

            tx.commit();
            return DepartmentMapper.toDto(entity);
        }
        catch(RuntimeException ex)
        {
            if(tx.isActive()) tx.rollback();
            throw ex;
        }
        finally {
            em.close();
        }

    }

    @Override
    public void deleteById(Integer id)
    {
        if(id==null)
        {
            throw new IllegalArgumentException("ID rỗng, không thể xóa");
        }

        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Department found = em.find(Department.class, id);
            if (found == null) {
                throw new IllegalArgumentException("Khoa không tồn tại (id=" + id + ")");
            }

            // rule business: không cho xoá nếu còn bác sĩ thuộc khoa đó
            if (found.getDoctors() != null && !found.getDoctors().isEmpty()) {
                throw new IllegalStateException("Không thể xoá khoa còn bác sĩ đang thuộc khoa.");
            }

            deptRepo.deleteById(em, id);

            tx.commit();

        }catch (RuntimeException ex)
        {
            if(tx.isActive()) tx.rollback();
            throw ex;
        }finally {
            em.close();
        }
    }

    @Override
    public DepartmentDTO update(Integer id,DepartmentDTO dto)
    {
        return new DepartmentDTO();
    }

}
