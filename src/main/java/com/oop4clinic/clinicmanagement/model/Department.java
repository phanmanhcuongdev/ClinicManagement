package com.oop4clinic.clinicmanagement.model;

// Đây là model mới dựa trên bảng 'departments' của bạn
public class Department {
    private int id;
    private String name;
    private double baseFee;
    private String description;

    // Constructor
    public Department(int id, String name, double baseFee, String description) {
        this.id = id;
        this.name = name;
        this.baseFee = baseFee;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getBaseFee() {
        return baseFee;
    }
    public String getDescription() {
        return description;
    }
}