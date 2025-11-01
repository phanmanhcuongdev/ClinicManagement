package com.oop4clinic.clinicmanagement.model;

public class DoctorSchedule {
    private int id;
    private int doctorId;
    private String workDate;
    private String workTime;
    private String status; // "available" hoặc "off"

    public DoctorSchedule(int id, int doctorId, String workDate, String workTime, String status) {
        this.id = id;
        this.doctorId = doctorId;
        this.workDate = workDate;
        this.workTime = workTime;
        this.status = status;
    }

    public int getId() { return id; }
    public int getDoctorId() { return doctorId; }
    public String getWorkDate() { return workDate; }
    public String getWorkTime() { return workTime; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return workDate + " - " + workTime + " (" + status + ")";
    }
}
