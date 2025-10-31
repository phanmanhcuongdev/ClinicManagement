package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository {

    Appointment save(EntityManager em, Appointment appt);

    /**
     * Lấy các lịch hẹn trong khoảng [from, to],
     * so sánh theo startTime, tăng dần theo startTime.
     */
    List<Appointment> findByStartTimeRange(EntityManager em,
                                           LocalDateTime from,
                                           LocalDateTime to);

    /**
     * Lấy N lịch hẹn sắp tới sau thời điểm 'after',
     * sort theo startTime ASC (dùng cho Dashboard -> Lịch hẹn sắp tới).
     */
    List<Appointment> findUpcoming(EntityManager em,
                                   LocalDateTime after,
                                   int limit);

    /**
     * Đếm số lịch hẹn trong [from, to].
     * Dùng cho KPI "Lịch hẹn hôm nay".
     */
    long countInRange(EntityManager em,
                      LocalDateTime from,
                      LocalDateTime to);
    List<Appointment> findAllbyStatus(EntityManager em, AppointmentStatus status);
    List<Appointment> findAll(EntityManager em);
}
