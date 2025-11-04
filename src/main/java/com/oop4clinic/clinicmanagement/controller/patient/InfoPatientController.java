package com.oop4clinic.clinicmanagement.controller.patient;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.impl.PatientServiceImpl;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InfoPatientController implements Initializable {

    @FXML private TextField namePatient;
    @FXML private DatePicker birthdayPatient;
    @FXML private TextField idPatient;
    @FXML private RadioButton genderPatient1;
    @FXML private RadioButton genderPatient2;
    @FXML private TextField phonePatient;
    @FXML private TextField addressPatient;
    @FXML private TextField emailPatient;
    @FXML private TextField insurrancePatient;
    @FXML private Button saveButton;

    private ToggleGroup genderToggleGroup;
    private final PatientService patientService = new PatientServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderToggleGroup = new ToggleGroup();
        genderPatient1.setToggleGroup(genderToggleGroup);
        genderPatient2.setToggleGroup(genderToggleGroup);

        PatientDTO currentPatient = UserSession.getCurrentPatient();

        if (currentPatient != null) {
            namePatient.setText(currentPatient.getFullName());
            birthdayPatient.setValue(currentPatient.getDateOfBirth());
            idPatient.setText(currentPatient.getCccd());
            addressPatient.setText(currentPatient.getAddress());
            emailPatient.setText(currentPatient.getEmail());
            insurrancePatient.setText(currentPatient.getInsuranceCode());

            phonePatient.setText(currentPatient.getPhone());
            phonePatient.setEditable(false);

            if (currentPatient.getGender() == Gender.MALE) {
                genderToggleGroup.selectToggle(genderPatient1);
            } else if (currentPatient.getGender() == Gender.FEMALE) {
                genderToggleGroup.selectToggle(genderPatient2);
            }
        } else {
            System.err.println("⚠ Không có bệnh nhân!");
        }
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

        PatientDTO dto = new PatientDTO();
        dto.setId(UserSession.getCurrentPatient().getId());
        dto.setFullName(fullName);
        dto.setDateOfBirth(dateOfBirth);
        dto.setCccd(cccd);
        dto.setGender(Gender.valueOf(gender));
        dto.setPhone(phone);
        dto.setAddress(address);
        dto.setEmail(email);
        dto.setInsuranceCode(insuranceCode);

        try {
            patientService.update(dto);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin cá nhân thành công!");
            UserSession.setCurrentPatient(dto);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Cập nhật thông tin thất bại. Vui lòng kiểm tra lại.");
            e.printStackTrace();
        }
    }

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
        return cccd != null && cccd.matches("^\\d{12}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^0\\d{9}$");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true;
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    private String getSelectedGender() {
        RadioButton selected = (RadioButton) genderToggleGroup.getSelectedToggle();
        if (selected == null) return null;
        return "Nam".equals(selected.getText()) ? "MALE" : "FEMALE";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml"));
        UserSession.clear();
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
}
