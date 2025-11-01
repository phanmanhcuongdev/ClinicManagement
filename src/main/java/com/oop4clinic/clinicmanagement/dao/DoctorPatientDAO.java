package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorPatientDAO {
    private static final String URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";

    private static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Doctor getDoctorById(int doctorId) throws SQLException {
        String query = "SELECT d.*, dep.name AS department_name " +
                "FROM doctors d " +
                "JOIN departments dep ON d.department_id = dep.id " +
                "WHERE d.id = ?";

        try (Connection connection = connect()) {
            assert connection != null;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setInt(1, doctorId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new Doctor(
                                rs.getInt("id"),
                                rs.getInt("department_id"),
                                rs.getString("full_name"),
                                rs.getString("gender"),
                                rs.getString("date_of_birth"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("address"),
                                rs.getDouble("consultation_fee"),
                                rs.getString("department_name")
                        );
                    }
                }
            }
        }
        return null;
    }
    public List<Doctor> getAllDoctors() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT d.*, dep.name AS department_name " +
                "FROM doctors d " +
                "JOIN departments dep ON d.department_id = dep.id";

        try (Connection connection = connect()) {
            assert connection != null;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    doctors.add(new Doctor(
                            rs.getInt("id"),
                            rs.getInt("department_id"),
                            rs.getString("full_name"),
                            rs.getString("gender"),
                            rs.getString("date_of_birth"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getDouble("consultation_fee"),
                            rs.getString("department_name") // <-- LẤY TÊN KHOA
                    ));
                }
            }
        }
        return doctors;
    }

    public List<Doctor> getAvailableDoctorsFiltered(String nameQuery, String deptQuery) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT d.*, dep.name AS department_name " +
                        "FROM doctors d " +
                        "JOIN departments dep ON d.department_id = dep.id " +
                        "JOIN doctor_schedule ds ON d.id = ds.doctor_id " +
                        "WHERE ds.status = 'AVAILABLE' " // Chỉ lấy bác sĩ có lịch trống
        );

        if (nameQuery != null && !nameQuery.trim().isEmpty()) {
            sql.append("AND d.full_name LIKE ? ");
        }

        if (deptQuery != null && !deptQuery.trim().isEmpty()) {
            sql.append("AND dep.name LIKE ? ");
        }

        sql.append("ORDER BY d.full_name");

        try (Connection conn = connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;

                if (nameQuery != null && !nameQuery.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + nameQuery.trim() + "%");
                }

                if (deptQuery != null && !deptQuery.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + deptQuery.trim() + "%");
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        doctors.add(new Doctor(
                                rs.getInt("id"),
                                rs.getInt("department_id"),
                                rs.getString("full_name"),
                                rs.getString("gender"),
                                rs.getString("date_of_birth"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("address"),
                                rs.getDouble("consultation_fee"),
                                rs.getString("department_name")
                        ));
                    }
                }
            }
        }
        return doctors;
    }
}