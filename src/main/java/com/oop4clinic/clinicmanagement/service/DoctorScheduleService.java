package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DoctorScheduleDTO;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;

import java.util.List;

public interface DoctorScheduleService {
    // Lấy tất cả lịch CÓ SẴN (available) của một bác sĩ
    List<DoctorScheduleDTO> getAvailableSchedulesByDoctorId(int doctorId);


    // Cập nhật trạng thái (ví dụ: từ "AVAILABLE" -> "OFF")
    void updateScheduleStatus(int scheduleId, DoctorScheduleStatus newStatus);

    List<DoctorScheduleDTO> getAvailableSchedulesByDeptAndDate(Integer id, String dateStringQuery);
}