package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentPatientDAO {

    private static final String URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";

    private static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static List<Appointment> getAppointmentsByPatient(int patientId, String doctorName, String status, String date) {
        List<Appointment> list = new ArrayList<>();
        String dbStatus = convertStatusToDB(status);

        StringBuilder sql = new StringBuilder("""
            SELECT a.id,
                   a.patient_id,
                   p.full_name AS patient_name,
                   d.name AS department_name,
                   doc.full_name AS doctor_name,
                   a.appointment_date,
                   a.start_time,
                   a.status,
                   a.reason
            FROM appointments a
            JOIN patients p ON a.patient_id = p.id
            JOIN departments d ON a.department_id = d.id
            LEFT JOIN doctors doc ON a.doctor_id = doc.id
            WHERE a.patient_id = ?
        """);

        if (doctorName != null && !doctorName.isEmpty()) sql.append(" AND doc.full_name LIKE ? ");
        if (dbStatus != null && !dbStatus.isEmpty()) sql.append(" AND a.status = ? ");
        if (date != null && !date.isEmpty()) sql.append(" AND a.appointment_date LIKE ? ");
        sql.append(" ORDER BY a.appointment_date DESC, a.start_time DESC ");

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            pstmt.setInt(index++, patientId);
            if (doctorName != null && !doctorName.isEmpty()) pstmt.setString(index++, "%" + doctorName + "%");
            if (dbStatus != null && !dbStatus.isEmpty()) pstmt.setString(index++, dbStatus);
            if (date != null && !date.isEmpty()) pstmt.setString(index++, "%" + date + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getString("patient_name"),
                        rs.getString("department_name"),
                        rs.getString("doctor_name"),
                        rs.getString("appointment_date"),
                        rs.getString("start_time"),
                        convertStatus(rs.getString("status")),
                        rs.getString("reason")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Appointment> getAllAppointmentsByPatient(int patientId) {
        return getAppointmentsByPatient(patientId, "", "", "");
    }

    // cập nhật trạng thái cuộc hẹn
    public static void updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, convertStatusToDB(status));
            pstmt.setInt(2, appointmentId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String convertStatus(String dbStatus) {
        return switch (dbStatus) {
            case "SCHEDULED" -> "Sắp tới";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> dbStatus;
        };
    }

    private static String convertStatusToDB(String displayStatus) {
        return switch (displayStatus) {
            case "Sắp tới" -> "SCHEDULED";
            case "Hoàn thành" -> "COMPLETED";
            case "Đã hủy" -> "CANCELLED";
            default -> "";
        };
    }

    // thêm lịch hẹn mới
    public void createAppointment(int patientId, int departmentId, int doctorId,
                                  String appointmentDate, String startTime, String reason) throws SQLException {

        String query = "INSERT INTO appointments (patient_id, department_id, doctor_id, " +
                "appointment_date, start_time, reason) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            stmt.setInt(2, departmentId);
            stmt.setInt(3, doctorId);
            stmt.setString(4, appointmentDate);
            stmt.setString(5, startTime);
            stmt.setString(6, reason);

            stmt.executeUpdate();
        }
    }
}
