package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DepartmentRepository;
import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DepartmentRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
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

    @Override
    public List<DoctorDTO> searchDoctors(String keyword,
                                         Integer departmentId,
                                         DoctorStatus status) {

        EntityManager em = EntityManagerProvider.em();
        try {
            // 1. Lấy all từ repo (sau này bạn có thể thay bằng query có WHERE)
            var allEntities = doctorRepo.findAll(em);

            // 2. Map sang DTO trước cho dễ làm việc với field đã flatten (departmentName,...)
            List<DoctorDTO> allDtos = allEntities.stream()
                    .map(DoctorMapper::toDTO)
                    .toList();

            // 3. Lọc theo tiêu chí
            String kw = normalize(keyword);

            List<DoctorDTO> filtered = allDtos.stream()
                    .filter(d -> {
                        // keyword match: fullName / phone / email (contains, ignorecase)
                        if (kw != null) {
                            String fn = normalize(d.getFullName());
                            String ph = normalize(d.getPhone());
                            String eml = normalize(d.getEmail());

                            boolean hit =
                                    (fn != null && fn.contains(kw)) ||
                                    (ph != null && ph.contains(kw)) ||
                                    (eml != null && eml.contains(kw));

                            if (!hit) return false;
                        }

                        // department filter
                        if (departmentId != null) {
                            if (d.getDepartmentId() == null ||
                                !d.getDepartmentId().equals(departmentId)) {
                                return false;
                            }
                        }

                        // status filter
                        if (status != null) {
                            if (d.getDoctorStatus() != status) {
                                return false;
                            }
                        }

                        return true;
                    })
                    // 4. sort theo tên A→Z không phân biệt hoa/thường
                    .sorted(Comparator.comparing(
                            d -> safeLower(d.getFullName())
                    ))
                    .toList();

            // 5. trả về list đã lọc
            return new ArrayList<>(filtered);

        } finally {
            em.close();
        }
    }

    // Helper nhỏ để tránh null handling lặp lại
    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.toLowerCase();
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}
