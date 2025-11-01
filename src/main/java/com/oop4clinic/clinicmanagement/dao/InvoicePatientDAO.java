package com.oop4clinic.clinicmanagement.dao;

import com.oop4clinic.clinicmanagement.model.Invoice;

import java.sql.Connection;
import java.sql.DriverManager;
// (SỬA) Thêm PreparedStatement
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InvoicePatientDAO {
    private static final String URL = "jdbc:sqlite:C:/Users/dangh/clinic_management.db";

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }

    public static List<Invoice> getInvoicesByPatientId(int patientId) {
        String sql = "SELECT id, details, total, status FROM invoices WHERE patient_id = ?";

        List<Invoice> invoiceList = new ArrayList<>();
        int orderCounter = 1;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String details = rs.getString("details");
                    double total = rs.getDouble("total");
                    String statusFromDb = rs.getString("status");

                    int orderNumber = orderCounter++;
                    String invoiceId = "INV-" + id;
                    String description = details;
                    double amountDue = total;
                    String status;
                    double amountPaid;

                    switch (statusFromDb) {
                        case "PAID":
                            status = "Đã thanh toán đủ";
                            amountPaid = total;
                            break;
                        case "PENDING":
                            status = "Chưa thanh toán";
                            amountPaid = 0;
                            break;
                        case "CANCELLED":
                            status = "Đã hủy";
                            amountPaid = 0;
                            break;
                        default:
                            status = "Không xác định";
                            amountPaid = 0;
                            break;
                    }

                    Invoice invoice = new Invoice(orderNumber, invoiceId, description, status, amountDue, amountPaid);
                    invoiceList.add(invoice);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error querying data: " + e.getMessage());
        }
        return invoiceList;
    }
}