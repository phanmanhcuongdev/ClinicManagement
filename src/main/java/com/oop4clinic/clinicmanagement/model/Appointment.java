package com.oop4clinic.clinicmanagement.model;

public class Appointment {
    private int id;
    private int patientId;
    private String patientName;
    private String departmentName;
    private String doctorName;
    private String appointmentDate;
    private String startTime;
    private String status;
    private String reason;

    public Appointment(int id, int patientId, String patientName, String departmentName,
                       String doctorName, String appointmentDate, String startTime,
                       String status, String reason) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.departmentName = departmentName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.status = status;
        this.reason = reason;
    }

    // hien thi du lieu bang
    public Appointment(int patientId, String departmentName, String doctorName,
                       String appointmentDate, String startTime, String status, String reason) {
        this.patientId = patientId;
        this.departmentName = departmentName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.status = status;
        this.reason = reason;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDepartmentName() { return departmentName; }
    public String getDoctorName() { return doctorName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getStartTime() { return startTime; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }

    public void setStatus(String status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }
}
