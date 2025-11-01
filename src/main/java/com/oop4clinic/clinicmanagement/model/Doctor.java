package com.oop4clinic.clinicmanagement.model;

public class Doctor {
    private int id;
    private int departmentId;
    private String fullName;
    private String gender;
    private String dateOfBirth;
    private String phone;
    private String email;
    private String address;
    private double consultationFee;

    private String departmentName; // <-- THÊM TRƯỜNG MỚI

    // --- SỬA LẠI HÀM CONSTRUCTOR ---
    public Doctor(int id, int departmentId, String fullName, String gender, String dateOfBirth,
                  String phone, String email, String address, double consultationFee,
                  String departmentName) { // <-- THÊM THAM SỐ MỚI
        this.id = id;
        this.departmentId = departmentId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.consultationFee = consultationFee;
        this.departmentName = departmentName; // <-- GÁN GIÁ TRỊ MỚI
    }

    // --- Getters (Giữ nguyên các hàm cũ) ---
    public int getId() { return id; }
    public int getDepartmentId() { return departmentId; }
    public String getFullName() { return fullName; }
    public double getConsultationFee() { return consultationFee; }
    public String getGender() { return gender; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }

    // --- THÊM GETTER MỚI NÀY VÀO ---
    public String getDepartmentName() {
        return this.departmentName;
    }

    public String toString() {
        return this.fullName;
    }
}