package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    List<AppointmentDTO> getAll();
    List<AppointmentDTO> getAppointmentsForToday(int doctorId);
    void completeAppointment(int appointmentId);
    List<AppointmentDTO> getAppointmentsForDoctorByDate(int doctorId, LocalDate date);
    List<AppointmentDTO> getAllAppointmentsForDoctor(int doctorId);
}
