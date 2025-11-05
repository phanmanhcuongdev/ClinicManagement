package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.DoctorSchedule;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository {

    boolean existsByDoctorAndWorkDateAndTime(EntityManager em, Doctor doctor, LocalDate workDate, LocalTime startTime);

    Optional<DoctorSchedule> findById(EntityManager em, int id);

    // Phương thức tìm lịch trống theo ID Bác sĩ
    List<DoctorSchedule> findAvailableByDoctorId(EntityManager em, int doctorId);

    DoctorSchedule save(EntityManager em, DoctorSchedule schedule);

    // PHƯƠNG THỨC QUAN TRỌNG: SỬ DỤNG LOCALDATE
    List<DoctorSchedule> findAvailableByDeptAndDate(
            EntityManager em,
            int departmentId,
            LocalDate workDate // <-- Kiểu dữ liệu đúng cho Entity
    );
}