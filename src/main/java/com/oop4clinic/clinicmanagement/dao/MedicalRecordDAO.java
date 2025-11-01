package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.MedicalRecord;
import com.oop4clinic.clinicmanagement.model.MedicalRecordDetail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
    private static final String DATABASE_URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";

    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = new ArrayList<>();

        String sql = "SELECT mr.id, mr.created_at, mr.diagnosis, d.name as department_name " +
                "FROM medical_records mr " +
                "LEFT JOIN appointments a ON mr.appointment_id = a.id " +
                "LEFT JOIN departments d ON a.department_id = d.id";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int counter = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("created_at");
                String diagnosis = rs.getString("diagnosis");
                String department = rs.getString("department_name") != null ? rs.getString("department_name") : "N/A";
                records.add(new MedicalRecord(id, counter++, date, department, diagnosis));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return records;
    }

    // lấy hồ sơ chi tiết
    public MedicalRecordDetail getRecordDetailsById(int recordId) {
        String sql = "SELECT mr.*, doc.full_name as doctor_name, dept.name as department_name " +
                "FROM medical_records mr " +
                "LEFT JOIN doctors doc ON mr.doctor_id = doc.id " +
                "LEFT JOIN appointments a ON mr.appointment_id = a.id " +
                "LEFT JOIN departments dept ON a.department_id = dept.id " + // Sử dụng a.department_id
                "WHERE mr.id = ?";

        MedicalRecordDetail detail = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                detail = new MedicalRecordDetail(
                        rs.getInt("id"),
                        rs.getString("created_at"),
                        rs.getString("doctor_name") != null ? rs.getString("doctor_name") : "N/A",
                        rs.getString("department_name") != null ? rs.getString("department_name") : "N/A",
                        rs.getString("symptoms"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            System.err.println(recordId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return detail;
    }
}