package com.oop4clinic.clinicmanagement.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Lớp Model đại diện cho một hàng trong TableView Hồ sơ Khám bệnh.
 * Chứa các thông tin cơ bản và ID để xác định hồ sơ.
 */
public class MedicalRecord {
    private final SimpleIntegerProperty id; // ID của hồ sơ bệnh án
    private final SimpleIntegerProperty tt; // Số thứ tự
    private final SimpleStringProperty examinationDate; // Ngày khám
    private final SimpleStringProperty department; // Khoa
    private final SimpleStringProperty diagnosis; // Chuẩn đoán

    public MedicalRecord(int id, int tt, String examinationDate, String department, String diagnosis) {
        this.id = new SimpleIntegerProperty(id);
        this.tt = new SimpleIntegerProperty(tt);
        this.examinationDate = new SimpleStringProperty(examinationDate);
        this.department = new SimpleStringProperty(department);
        this.diagnosis = new SimpleStringProperty(diagnosis);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public int getTt() {
        return tt.get();
    }

    public String getExaminationDate() {
        return examinationDate.get();
    }

    public String getDepartment() {
        return department.get();
    }

    public String getDiagnosis() {
        return diagnosis.get();
    }

    // Property Getters (Cần thiết cho TableView)
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleIntegerProperty ttProperty() {
        return tt;
    }

    public SimpleStringProperty examinationDateProperty() {
        return examinationDate;
    }

    public SimpleStringProperty departmentProperty() {
        return department;
    }

    public SimpleStringProperty diagnosisProperty() {
        return diagnosis;
    }
}