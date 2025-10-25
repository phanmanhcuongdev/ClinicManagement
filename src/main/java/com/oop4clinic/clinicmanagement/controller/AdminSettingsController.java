package com.oop4clinic.clinicmanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminSettingsController {

    // ===== Header status =====
    @FXML private Label lblStatus;

    // ===== Giờ làm việc & slot khám =====
    @FXML private TextField txtMorningStart;
    @FXML private TextField txtMorningEnd;
    @FXML private TextField txtAfternoonStart;
    @FXML private TextField txtAfternoonEnd;
    @FXML private TextField txtSlotMinutes;
    @FXML private CheckBox chkWeekend;

    // ===== Chính sách khám & hủy =====
    @FXML private TextField txtDefaultFee;
    @FXML private TextField txtCancelMinHours;
    @FXML private CheckBox chkLateCancelFee;
    @FXML private TextField txtLateCancelFeeAmount;

    @FXML
    private void initialize() {
        // giá trị demo mặc định khi mở màn hình
        txtMorningStart.setText("08:00");
        txtMorningEnd.setText("11:30");
        txtAfternoonStart.setText("13:30");
        txtAfternoonEnd.setText("17:00");
        txtSlotMinutes.setText("15");
        chkWeekend.setSelected(false);

        txtDefaultFee.setText("200000");
        txtCancelMinHours.setText("2");
        chkLateCancelFee.setSelected(true);
        txtLateCancelFeeAmount.setText("50000");

        lblStatus.setText("Chưa lưu thay đổi");
    }

    @FXML
    private void onSave() {
        // Ở bản tạm thời này ta chỉ giả lập lưu,
        // không ghi DB, không ghi file. Chỉ báo cho user biết là "đã lưu".
        lblStatus.setText("Đã lưu cấu hình (tạm thời)");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lưu cấu hình");
        alert.setHeaderText(null);
        alert.setContentText("Cấu hình đã được lưu (demo).");
        alert.showAndWait();
    }
}
