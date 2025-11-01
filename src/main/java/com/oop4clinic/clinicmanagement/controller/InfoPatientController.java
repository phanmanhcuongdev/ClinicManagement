package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.dao.InfoPatientDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InfoPatientController implements Initializable {
    @FXML private TextField namePatient;
    @FXML private DatePicker birthdayPatient;
    @FXML private TextField idPatient; // CCCD
    @FXML private RadioButton genderPatient1; // Nam
    @FXML private RadioButton genderPatient2; // Nữ
    @FXML private TextField phonePatient;
    @FXML private TextField addressPatient;
    @FXML private TextField emailPatient;
    @FXML private TextField insurrancePatient;
    @FXML private Button saveButton;

    @FXML private MenuItem infoPatient;
    @FXML private MenuItem recordPatient;
    @FXML private MenuItem billPatient;
    @FXML private MenuItem appointmentPatient;
    @FXML private MenuItem logoutPatient;
    @FXML private Button homeButton;

    private ToggleGroup genderToggleGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderToggleGroup = new ToggleGroup();
        genderPatient1.setToggleGroup(genderToggleGroup);
        genderPatient2.setToggleGroup(genderToggleGroup);
    }

    @FXML
    void handleSave(ActionEvent event) {
        String fullName = namePatient.getText();
        LocalDate dateOfBirth = birthdayPatient.getValue();
        String cccd = idPatient.getText();
        String phone = phonePatient.getText();
        String address = addressPatient.getText();
        String email = emailPatient.getText();
        String insuranceCode = insurrancePatient.getText();
        String gender = getSelectedGender();

        if (!isInputValid(fullName, dateOfBirth, cccd, phone, email, gender)) {
            return;
        }

        boolean success = InfoPatientDAO.createPatient(
                fullName,
                String.valueOf(dateOfBirth),
                cccd,
                gender,
                phone,
                address,
                email,
                insuranceCode
        );


        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu thông tin bệnh nhân mới thành công!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Lưu thông tin thất bại. Vui lòng kiểm tra lại, có thể CCCD hoặc SĐT đã tồn tại.");
        }
    }

    // kiểm tra định dạng nhập dữ liệu
    private boolean isInputValid(String fullName, LocalDate dob, String cccd, String phone, String email, String gender) {
        if (fullName == null || fullName.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đầu vào", "Họ và tên không được để trống.");
            return false;
        }
        if (dob == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đầu vào", "Vui lòng chọn ngày sinh.");
            return false;
        }
        if (!isValidCccd(cccd)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Căn cước công dân phải gồm đúng 12 chữ số.");
            return false;
        }
        if (!isValidPhone(phone)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Số điện thoại không hợp lệ (phải có 10 số và bắt đầu bằng 0).");
            return false;
        }
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Địa chỉ email không hợp lệ.");
            return false;
        }
        if (gender == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đầu vào", "Vui lòng chọn giới tính.");
            return false;
        }
        return true;
    }


    private boolean isValidCccd(String cccd) {
        if (cccd == null) return false;
        return cccd.matches("\\d{12}");
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("^0\\d{9}$");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private String getSelectedGender() {
        RadioButton selected = (RadioButton) genderToggleGroup.getSelectedToggle();
        if (selected == null) return null;
        return "Nam".equals(selected.getText()) ? "MALE" : "FEMALE";
    }

    private void clearForm() {
        namePatient.clear();
        birthdayPatient.setValue(null);
        idPatient.clear();
        phonePatient.clear();
        addressPatient.clear();
        emailPatient.clear();
        insurrancePatient.clear();
        genderToggleGroup.selectToggle(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // chuyen trang
    @FXML void handleInfo(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Logout.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Book1.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
}