package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.impl.DoctorServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.PatientServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.AuthService;
import com.oop4clinic.clinicmanagement.util.SessionManager;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
// Xóa import Label, thêm import Hyperlink
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Đã XÓA: @FXML private Label statusLabel;

    // ĐÃ THÊM: Liên kết tới Hyperlink từ FXML
    @FXML
    private Hyperlink registerLink;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String pass = passwordField.getText();

        AuthService authService = new AuthService();

        try {
            User user = authService.login(username, pass);

            // Tạm thời vô hiệu hóa link để hiển thị trạng thái
            registerLink.setDisable(true);
            setLabelStatus(Color.GREEN, "Đăng nhập thành công!");

            String url = "";

            if (user.getRole().equals(UserRole.PATIENT)) {
                // --- Tìm Patient theo số điện thoại ---
                PatientService patientService = new PatientServiceImpl();
                PatientDTO patient = patientService.findByPhone(user.getUsername());

                if (patient != null) {
                    // --- Lưu session ---
                    SessionManager.login(patient.getId(), patient.getPhone());
                    System.out.println("Đăng nhập thành công: ID bệnh nhân = " + patient.getId());
                } else {
                    System.err.println("Không tìm thấy bệnh nhân với số điện thoại: " + user.getUsername());
                }
                UserSession.setCurrentUser(user);
                url = "/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml";
            }

            if (user.getRole().equals(UserRole.DOCTOR)) {
                DoctorServiceImpl doctorService = new DoctorServiceImpl();
                DoctorDTO doctor = doctorService.findByPhone(user.getUsername());

                if(doctor != null){
                    SessionManager.login(doctor.getId(), doctor.getPhone());
                    System.out.println("Đăng nhập thành công: ID bác sĩ = " + doctor.getId());
                } else {
                    System.err.println("Không tìm thấy bác sĩ với số điện thoại: " + user.getUsername());
                }
                url = "/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml";
            }

            if (user.getRole().equals(UserRole.ADMIN)) {
                url = "/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml";
            }

            loadScene(url);

        } catch (Exception e) {
            // Hiển thị lỗi trên link
            setLabelStatus(Color.RED, e.getMessage());
        }
    }

    private void setLabelStatus(Color color, String message) {
        // Phương thức này giờ sẽ cập nhật 'registerLink'
        // thay vì 'statusLabel'
        registerLink.setTextFill(color);
        registerLink.setText(message);
    }

    private void loadScene(String url) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) usernameField).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Register.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}