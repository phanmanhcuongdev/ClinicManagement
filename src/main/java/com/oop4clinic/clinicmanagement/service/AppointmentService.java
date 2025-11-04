package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.CreateAppointmentDTO;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentDTO updateStatus(Integer appointmentId, AppointmentStatus newStatus);

    List<AppointmentDTO> getAll();

    List<AppointmentDTO> findAppointmentsByPatientId(Integer patientId);

    List<AppointmentDTO> searchAppointmentsByPatient(Integer patientId, String doctorName, AppointmentStatus status, LocalDate date);

    void createAppointment(CreateAppointmentDTO dto);

    List<AppointmentDTO> getAppointmentsForToday(int doctorId);
    void completeAppointment(int appointmentId);
    List<AppointmentDTO> getAppointmentsForDoctorByDate(int doctorId, LocalDate date);
    List<AppointmentDTO> getAllAppointmentsForDoctor(int doctorId);
}
