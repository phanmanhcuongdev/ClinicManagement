package com.oop4clinic.clinicmanagement.model;

/**
 \
 */
public class MedicalRecordDetail {
    private final int id;
    private final String createdAt;
    private final String doctorName;      // Thay vì doctorId
    private final String department;      // Tên khoa
    private final String symptoms;
    private final String diagnosis;
    private final String prescription;
    private final String notes;

    public MedicalRecordDetail(int id, String createdAt, String doctorName, String department, String symptoms, String diagnosis, String prescription, String notes) {
        this.id = id;
        this.createdAt = createdAt;
        this.doctorName = doctorName;
        this.department = department;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
    }

    // Getters
    public int getId() { return id; }
    public String getCreatedAt() { return createdAt; }
    public String getDoctorName() { return doctorName; }
    public String getDepartment() { return department; }
    public String getSymptoms() { return symptoms; }
    public String getDiagnosis() { return diagnosis; }
    public String getPrescription() { return prescription; }
    public String getNotes() { return notes; }
}