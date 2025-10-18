package com.oop4clinic.clinicmanagement.service.impl;

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
//    @Override
//    public List<DepartmentDTO> findAll()
//    {
//        EntityManager em = EntityManagerProvider.em();
//        try {
//            var deps = em.createQuery("select d from Department d order by d.name", Department.class)
//                         .getResultList();
//            return deps.stream().map(DepartmentMapper::toDTO).collect(Collectors.toList());
//        } finally {
//            em.close();
//        }
//    }
//    @Override
//    public DepartmentDTO create(DepartmentDTO dto) {
//        EntityManager em = EntityManagerProvider.em();
//        EntityTransaction tx = em.getTransaction();
//        try {
//            tx.begin();
//
//            // Unique theo name (nếu DB set unique) – check sớm để báo lỗi UI đẹp:
//            Long cnt = em.createQuery("select count(d) from Department d where lower(d.name)=:n", Long.class)
//                    .setParameter("n", dto.getName().toLowerCase().trim())
//                    .getSingleResult();
//            if (cnt > 0) throw new IllegalArgumentException("Tên khoa đã tồn tại");
//
//            Department entity = DepartmentMapper.toEntity(dto);
//            em.persist(entity);
//
//            tx.commit();
//            return DepartmentMapper.toDTO(entity);
//        } catch (RuntimeException ex) {
//            if (tx.isActive()) tx.rollback();
//            throw ex;
//        } finally {
//            em.close();
//        }
//    }
    @Override
    public List<DepartmentDTO> findAll()
    {
        EntityManager em = EntityManagerProvider.em();
        try {
            var list = em.createQuery(
                "select d from Department d order by d.id", Department.class
            ).getResultList();

            return list.stream()
                       .map(DepartmentMapper::toDTO)
                       .toList();
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

            Long cnt = em.createQuery("select count(d) from Department d where lower(d.name)=:n", Long.class)
                .setParameter("n", dto.getName().toLowerCase().trim())
                .getSingleResult();

            if(cnt>0) throw new IllegalArgumentException("Tên khoa đã tồn tại");

            Department entity = DepartmentMapper.toEntity(dto);
            em.persist(entity);

            tx.commit();
            return DepartmentMapper.toDTO(entity);
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
}
