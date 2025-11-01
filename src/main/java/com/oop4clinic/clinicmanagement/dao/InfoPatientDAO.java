package com.oop4clinic.clinicmanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InfoPatientDAO {
    private static final String URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";

    private static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            System.out.println(URL);
            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static boolean updatePatient(String fullName, String dateOfBirth, String cccd,
                                        String gender, String phone, String address,
                                        String email, String insuranceCode) {

        String sql = "UPDATE patients SET " +
                "full_name = ?, " +
                "gender = ?, " +
                "date_of_birth = ?, " +
                "email = ?, " +
                "address = ?, " +
                "cccd = ?, " +
                "insurance_code = ? " +
                "WHERE phone = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, gender);
            pstmt.setString(3, dateOfBirth);
            pstmt.setString(4, email);
            pstmt.setString(5, address);
            pstmt.setString(6, cccd);
            pstmt.setString(7, insuranceCode);
            pstmt.setString(8, phone.trim());

            int rows = pstmt.executeUpdate();
            System.out.println(rows);
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // thêm mới bệnh nhân
    public static boolean createPatient(String fullName, String dateOfBirth, String cccd,
                                        String gender, String phone, String address,
                                        String email, String insuranceCode) {

        String sql = "INSERT INTO patients(full_name, gender, date_of_birth, phone, email, address, cccd, insurance_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, gender);
            pstmt.setString(3, dateOfBirth);
            pstmt.setString(4, phone.trim());
            pstmt.setString(5, email);
            pstmt.setString(6, address);
            pstmt.setString(7, cccd);
            pstmt.setString(8, insuranceCode);

            pstmt.executeUpdate();
            System.out.println("Thêm bệnh nhân mới thành công.");
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
