package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DoctorScheduleDTO;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorScheduleService {
    // Lấy tất cả lịch CÓ SẴN (available) của một bác sĩ
    List<DoctorScheduleDTO> getAvailableSchedulesByDoctorId(int doctorId);


    // Cập nhật trạng thái (ví dụ: từ "AVAILABLE" -> "OFF")
    void updateScheduleStatus(int scheduleId, DoctorScheduleStatus newStatus);

    List<DoctorScheduleDTO> getAvailableSchedulesByDeptAndDate(Integer id, String dateStringQuery);

    int createSchedulesForDate(LocalDate date);

    void ensureSchedulesExistForNextDays(int days);
}