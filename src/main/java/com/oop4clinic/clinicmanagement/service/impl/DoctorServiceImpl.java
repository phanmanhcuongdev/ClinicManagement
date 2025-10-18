package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DepartmentRepository;
import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DepartmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.mapper.DoctorMapper;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import jakarta.persistence.*;
import java.util.*;

public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepo = new DoctorRepositoryImpl();
    private final DepartmentRepository deptRepo = new DepartmentRepositoryImpl();

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
                if(doctorRepo.existsByPhone(em, dto.getPhone().trim()))
                    throw new IllegalArgumentException("SĐT đã tồn tại");
            }

            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                if(doctorRepo.existsByEmail(em,dto.getEmail().trim()))
                    throw new IllegalArgumentException("Email đã tồn tại");
            }

            // ===== Tham chiếu Department bằng id trong DTO =====
            Department dep = deptRepo.findById(em,dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Khoa không tòn tại"));

            // ===== Map DTO -> Entity & persist =====
            Doctor entity = DoctorMapper.toEntityForCreate(dto,dep);
            Doctor saved = doctorRepo.save(em,entity);

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
            var list = doctorRepo.findAll(em);

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
