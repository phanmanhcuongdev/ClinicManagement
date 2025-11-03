package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.PatientRepository;
import com.oop4clinic.clinicmanagement.dao.impl.PatientRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.model.mapper.PatientMapper;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.query.PageRequest;
import com.oop4clinic.clinicmanagement.service.query.PageResult;
import com.oop4clinic.clinicmanagement.service.query.PatientFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepo = new PatientRepositoryImpl();

    @Override
    public PatientDTO create(PatientDTO dto) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Patient entity = PatientMapper.toEntity(dto);
            Patient saved = patientRepo.create(em, entity);
            tx.commit();
            return PatientMapper.toDto(saved);
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<PatientDTO> findAll() {
        EntityManager em = EntityManagerProvider.em();
        try {
            var list = PatientMapper.toDtoList(patientRepo.findAll(em));
            list.sort(Comparator.comparing(p -> {
                String name = p.getFullName();
                return name == null ? "" : name.toLowerCase();
            }));
            return list;
        } finally {
            em.close();
        }
    }

    @Override
    public PatientDTO update(PatientDTO dto) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Patient entity = PatientMapper.toEntity(dto);
            Patient updated = patientRepo.update(em, entity);
            tx.commit();
            return PatientMapper.toDto(updated);
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<PatientDTO> findByFilter(PatientFilter f) {
        var all = findAll();
        String kw = f.getKeyword() == null ? "" : f.getKeyword().trim().toLowerCase();

        return all.stream()
                .filter(p -> kw.isEmpty()
                        || (p.getFullName() != null && p.getFullName().toLowerCase().contains(kw))
                        || (p.getPhone() != null && p.getPhone().toLowerCase().contains(kw))
                        || (p.getEmail() != null && p.getEmail().toLowerCase().contains(kw))
                        || (p.getInsuranceCode() != null && p.getInsuranceCode().toLowerCase().contains(kw))
                        || (p.getAddress() != null && p.getAddress().toLowerCase().contains(kw)))
                .filter(p -> f.getGender() == null || p.getGender() == f.getGender())
                .filter(p -> f.getDobFrom() == null || (p.getDateOfBirth() != null && !p.getDateOfBirth().isBefore(f.getDobFrom())))
                .filter(p -> f.getDobTo() == null || (p.getDateOfBirth() != null && !p.getDateOfBirth().isAfter(f.getDobTo())))
                .toList();
    }

    @Override
    public PageResult<PatientDTO> findByFilter(PatientFilter filter, PageRequest pr) {
        var filtered = findByFilter(filter);
        int from = Math.max(0, pr.getPage() * pr.getSize());
        int to = Math.min(filtered.size(), from + pr.getSize());
        var pageList = from >= filtered.size()
                ? List.<PatientDTO>of()
                : filtered.subList(from, to);

        int totalPages = (int) Math.ceil((double) filtered.size() / pr.getSize());
        PageResult<PatientDTO> rs = new PageResult<>();
        rs.setContent(pageList);
        rs.setTotalElements(filtered.size());
        rs.setPage(pr.getPage());
        rs.setSize(pr.getSize());
        rs.setTotalPages(totalPages);
        return rs;
    }

    // ✅ Mới thêm: tìm bệnh nhân theo số điện thoại
    @Override
    public PatientDTO findByPhone(String phone) {
        EntityManager em = EntityManagerProvider.em();
        try {
            Optional<Patient> optional = patientRepo.findByPhone(em, phone);
            return optional.map(PatientMapper::toDto).orElse(null);
        } finally {
            em.close();
        }
    }
}
