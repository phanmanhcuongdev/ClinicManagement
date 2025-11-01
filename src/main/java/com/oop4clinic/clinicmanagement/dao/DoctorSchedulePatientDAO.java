package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.DoctorSchedule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorSchedulePatientDAO {
    private static final String URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";

    private static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Kết nối thất bại: " + e.getMessage());
            return null;
        }
    }

    public List<DoctorSchedule> getSchedulesByDoctor(int doctorId) {
        List<DoctorSchedule> schedules = new ArrayList<>();
        String query = "SELECT * FROM doctor_schedule WHERE doctor_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Bỏ qua nếu OFF
                if ("AVAILABLE".equalsIgnoreCase(rs.getString("status"))) {
                    schedules.add(new DoctorSchedule(
                            rs.getInt("id"),
                            rs.getInt("doctor_id"),
                            rs.getString("work_date"),
                            rs.getString("start_time"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public void updateScheduleStatus(int scheduleId, String newStatus) throws SQLException {
        String query = "UPDATE doctor_schedule SET status = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, scheduleId);
            stmt.executeUpdate();
        }
    }
    public List<DoctorSchedule> getSchedulesByDepartment(int departmentId) throws SQLException {
        List<DoctorSchedule> schedules = new ArrayList<>();

        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.start_time, ds.status " +
                "FROM doctor_schedule ds " +
                "JOIN doctors d ON ds.doctor_id = d.id " +
                "WHERE d.department_id = ? AND ds.status = 'AVAILABLE' " +
                "ORDER BY ds.work_date, ds.start_time";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    schedules.add(new DoctorSchedule(
                            rs.getInt("id"),
                            rs.getInt("doctor_id"),
                            rs.getString("work_date"),
                            rs.getString("start_time"), // <-- Sửa 'start_time' nếu cần
                            rs.getString("status")
                    ));
                }
            }
        }
        return schedules;
    }
}
