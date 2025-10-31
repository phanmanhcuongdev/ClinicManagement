package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface DashBoradService {
    long countPaitent();
    long countDoctor();
    long countCompletedAP();
    long countPendingAP();
    List<AppointmentDTO> getUpcomingAppointments();
    List<AppointmentDTO> getWeeklyAppointments(LocalDateTime start, LocalDateTime end);
}
