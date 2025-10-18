package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.mapper.DoctorMapper;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import jakarta.persistence.*;
import java.util.*;

public class DoctorServiceImpl implements DoctorService {
    @Override
    public DoctorDTO create(DoctorDTO dto)
    {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            //dto Validate

            if(dto == null) throw new IllegalArgumentException("Doctor rỗng");

            if(dto.getFullName() == null || dto.getFullName().isBlank())
                throw new IllegalArgumentException("Tên Bác sĩ rỗng");

            if(dto.getDepartmentId() == null)
                throw new IllegalArgumentException("Khoa rỗng");

            if(dto.getPhone() != null && !dto.getPhone().isBlank()){
                Long phoneCnt = em.createQuery(
                        "select count(d) from Doctor d where d.phone = :p", Long.class)
                        .setParameter("p", dto.getPhone().trim())
                        .getSingleResult();
                if (phoneCnt > 0) throw new IllegalArgumentException("SĐT đã tồn tại");
            }

            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                Long emailCnt = em.createQuery(
                        "select count(d) from Doctor d where lower(d.email) = :e", Long.class)
                        .setParameter("e", dto.getEmail().toLowerCase().trim())
                        .getSingleResult();
                if (emailCnt > 0) throw new IllegalArgumentException("Email đã tồn tại");
            }

            // ===== Tham chiếu Department bằng id trong DTO =====
            Department depRef = em.getReference(Department.class, dto.getDepartmentId());

            // ===== Map DTO -> Entity & persist =====
            Doctor entity = DoctorMapper.toEntityForCreate(dto, depRef);
            em.persist(entity);

            tx.commit();

            // ===== Trả về DTO sau khi lưu (có id, kèm tên khoa) =====
            return DoctorMapper.toDTO(entity);
        }catch (RuntimeException ex)
        {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally{
            em.close();
        }
    }

    @Override
    public List<DoctorDTO> findAll()
    {
        EntityManager em = EntityManagerProvider.em();
        try {
            // fetch department để có tên khoa khi map DTO
            var list = em.createQuery(
                "select d from Doctor d join fetch d.department order by d.id", Doctor.class
            ).getResultList();

            return list.stream()
                    .map(DoctorMapper::toDTO)
                    .toList();
        }
        catch(RuntimeException ex)
        {
            throw ex;
        }
        finally {
            em.close();
        }
    }
}
