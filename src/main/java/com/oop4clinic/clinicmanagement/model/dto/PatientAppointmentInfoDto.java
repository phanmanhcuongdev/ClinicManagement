package com.oop4clinic.clinicmanagement.model.dto;

import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientAppointmentInfoDto {
    private Integer patientId;
    private String patientFullName;
    private Gender patientGender;
    private LocalDate patientDateOfBirth;
    private String patientPhone;

    private LocalDateTime appointmentTime;
    private AppointmentStatus appointmentStatus;
    public PatientAppointmentInfoDto(PatientDTO patient, AppointmentDTO appointment) {
        if (patient != null) {
            this.patientId = patient.getId();
            this.patientFullName = patient.getFullName();
            this.patientGender = patient.getGender();
            this.patientDateOfBirth = patient.getDateOfBirth();
            this.patientPhone = patient.getPhone();
        }
        if (appointment != null) {
            this.appointmentTime = appointment.getStartTime();
            this.appointmentStatus = appointment.getStatus();
        }
    }
    public Integer getPatientId() { return patientId; }
    public String getPatientFullName() { return patientFullName; }
    public Gender getPatientGender() { return patientGender; }
    public LocalDate getPatientDateOfBirth() { return patientDateOfBirth; }
    public String getPatientPhone() { return patientPhone; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public AppointmentStatus getAppointmentStatus() { return appointmentStatus; }
}