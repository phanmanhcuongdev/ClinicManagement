package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DoctorScheduleRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorScheduleRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DoctorScheduleDTO;
import com.oop4clinic.clinicmanagement.model.entity.DoctorSchedule;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;
import com.oop4clinic.clinicmanagement.model.mapper.DoctorScheduleMapper;
import com.oop4clinic.clinicmanagement.service.DoctorScheduleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate; // <-- THÊM IMPORT
import java.time.format.DateTimeFormatter; // <-- THÊM IMPORT
import java.util.List;
import java.util.stream.Collectors;

public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepo = new DoctorScheduleRepositoryImpl();

    // Định dạng ngày phải khớp với định nghĩa trong Booking2Controller (yyyy-MM-dd)
    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<DoctorScheduleDTO> getAvailableSchedulesByDoctorId(int doctorId) {
        EntityManager em = EntityManagerProvider.em();
        try {
            List<DoctorSchedule> entities = scheduleRepo.findAvailableByDoctorId(em, doctorId);

            return entities.stream()
                    .map(DoctorScheduleMapper::toDTO)
                    .collect(Collectors.toList());

        } finally {
            em.close();
        }
    }

    @Override
    public void updateScheduleStatus(int scheduleId, DoctorScheduleStatus newStatus) {
        EntityManager em = EntityManagerProvider.em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            DoctorSchedule schedule = scheduleRepo.findById(em, scheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch (ID=" + scheduleId + ")"));

            schedule.setStatus(newStatus);
            scheduleRepo.save(em, schedule);

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<DoctorScheduleDTO> getAvailableSchedulesByDeptAndDate(Integer departmentId, String dateStringQuery) {
        if (departmentId == null || dateStringQuery == null) {
            return List.of();
        }

        // 1. CHUYỂN ĐỔI STRING SANG LOCALDATE
        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.parse(dateStringQuery, dbDateFormatter);
        } catch (Exception e) {
            System.err.println("Lỗi định dạng ngày khi parse: " + dateStringQuery);
            return List.of();
        }

        EntityManager em = EntityManagerProvider.em();
        try {
            // 2. TRUYỀN LOCALDATE VÀO REPOSITORY
            List<DoctorSchedule> entities = scheduleRepo.findAvailableByDeptAndDate(
                    em,
                    departmentId,
                    selectedDate // <-- Đã truyền LocalDate
            );

            return entities.stream()
                    .filter(s -> s.getStatus() == DoctorScheduleStatus.AVAILABLE)
                    .map(DoctorScheduleMapper::toDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Lỗi hệ thống khi tìm lịch theo Khoa và Ngày: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }
}