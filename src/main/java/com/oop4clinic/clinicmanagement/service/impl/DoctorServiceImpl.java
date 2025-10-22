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
            Doctor entity = DoctorMapper.toEntity(dto,dep);
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

    @Override
    public DoctorDTO findById(int id)
    {
        EntityManager em = EntityManagerProvider.em();
        try
        {
            var doctor = doctorRepo.findById(em,id);
            return DoctorMapper.toDTO(doctor);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public DoctorDTO update(DoctorDTO dto)
    {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();

        try
        {
            tx.begin();

            if(dto == null) throw new IllegalArgumentException("NULL DTO");
            if(dto.getId() == null) throw new IllegalArgumentException("Miss Id");

            Doctor existing = doctorRepo.findById(em,dto.getId());
            if(existing == null)
                throw new IllegalArgumentException("No Doctor "+ dto.getId());

            Department dep = null;

            if(dto.getDepartmentId()!=null)
            {
                dep = deptRepo.findById(em,dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Khoa không tòn tại"));
            }

            existing.setFullName(dto.getFullName());
            existing.setGender(dto.getGender());
            existing.setDateOfBirth(dto.getDateOfBirth());
            existing.setPhone(dto.getPhone());
            existing.setEmail(dto.getEmail());
            existing.setAddress(dto.getAddress());
            existing.setConsultationFee(dto.getConsultationFee());
            existing.setStatus(dto.getDoctorStatus());
            existing.setNotes(dto.getNotes());
            if (dep != null) existing.setDepartment(dep);

            Doctor updated = doctorRepo.update(em, existing);

            tx.commit();
            return DoctorMapper.toDTO(updated);

        }catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }


    }

}
