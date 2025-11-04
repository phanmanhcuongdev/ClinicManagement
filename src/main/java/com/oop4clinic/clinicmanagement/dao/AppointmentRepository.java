package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository {

    Appointment save(EntityManager em, Appointment appt);

    List<Appointment> findByStartTimeRange(EntityManager em, LocalDateTime from, LocalDateTime to);

    List<Appointment> findUpcoming(EntityManager em, LocalDateTime after, int limit);

    long countInRange(EntityManager em, LocalDateTime from, LocalDateTime to);

    List<Appointment> findAllbyStatus(EntityManager em, AppointmentStatus status);

    List<Appointment> findAll(EntityManager em);

    List<Appointment> findByPatientId(EntityManager em, Integer patientId);

    List<Appointment> searchByPatient(EntityManager em, Integer patientId, String doctorName, AppointmentStatus status, LocalDate date);
    List<Appointment> findAllByDoctorId(EntityManager em, int doctorId);

}
