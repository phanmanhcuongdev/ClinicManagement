package com.oop4clinic.clinicmanagement.model.entity;

import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
  name = "appointments",
  indexes = {
    @Index(name = "idx_appt_doctor_time", columnList = "doctor_id,startTime"),
    @Index(name = "idx_appt_patient_time", columnList = "patient_id,startTime")
  }
)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AppointmentStatus status;

    @Column(length = 255)
    private String reason;

    @PrePersist @PreUpdate
    private void validateTimes() {
        if (endTime != null && endTime.isBefore(startTime)) {
        throw new IllegalArgumentException("endTime must be after startTime");
        }
    }

    public Appointment() {}
    public Appointment(Patient patient,Doctor doctor,LocalDateTime startTime,LocalDateTime endTime,AppointmentStatus status,String reason)
    {
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + id +
            ", patientId=" + (patient != null ? patient.getId() : null) +
            ", doctorId=" + (doctor != null ? doctor.getId() : null) +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", status=" + status +
            ", reason='" + reason + '\'' +
            '}';
    }
}
