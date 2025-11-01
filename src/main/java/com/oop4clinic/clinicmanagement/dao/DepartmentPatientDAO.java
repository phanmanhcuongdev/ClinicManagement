package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.Department; // (Bạn phải tạo file Department.java)
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentPatientDAO {
    private static final String URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";
    private static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Department> getFilteredDepartments(String deptQuery, String dateQuery) throws SQLException {
        List<Department> departments = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT dep.id, dep.name, dep.base_fee, dep.description " +
                        "FROM departments dep " +
                        "JOIN doctors d ON dep.id = d.department_id " +
                        "JOIN doctor_schedule ds ON d.id = ds.doctor_id " +
                        "WHERE ds.status = 'AVAILABLE' " // Luôn lọc lịch trống
        );

        if (deptQuery != null && !deptQuery.trim().isEmpty()) {
            sql.append("AND dep.name LIKE ? ");
        }

        if (dateQuery != null && !dateQuery.trim().isEmpty()) {
            sql.append("AND ds.work_date = ? ");
        }

        sql.append("ORDER BY dep.name");

        try (Connection conn = connect()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                int paramIndex = 1;

                if (deptQuery != null && !deptQuery.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + deptQuery.trim() + "%");
                }

                if (dateQuery != null && !dateQuery.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, dateQuery);
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        departments.add(new Department(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("base_fee"),
                                rs.getString("description")
                        ));
                    }
                }
            }
        }
        return departments;
    }
}