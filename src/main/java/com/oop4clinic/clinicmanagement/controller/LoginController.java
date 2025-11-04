package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.impl.PatientServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.AuthService;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    @FXML
    private Hyperlink registerLink;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String pass = passwordField.getText();

        AuthService authService = new AuthService();

        try {
            User user = authService.login(username, pass);
            registerLink.setDisable(true);
            setLabelStatus(Color.GREEN, "Đăng nhập thành công!");
            // tbao
            String url = "";

            if (user.getRole() == UserRole.PATIENT) {
                PatientService patientService = new PatientServiceImpl();
                PatientDTO patient = patientService.findByPhone(user.getUsername());

                if (patient != null) {
                    // Đảm bảo thứ tự đúng: lưu User trước, Patient sau
                    UserSession.setCurrentUser(user);
                    UserSession.setCurrentPatient(patient);
                    System.out.println("Đăng nhập thành công: " + patient.getFullName() + " (ID=" + patient.getId() + ")");
                } else {
                    System.err.println("Không tìm thấy bệnh nhân với số điện thoại: " + user.getUsername());
                }

                url = "/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml";

            }

            if (user.getRole() == UserRole.DOCTOR) {
                UserSession.setCurrentUser(user);
                url = "/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml";
            }

            if (user.getRole() == UserRole.ADMIN) {
                UserSession.setCurrentUser(user);
                url = "/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml";
            }

            loadScene(url);

        } catch (Exception e) {
            setLabelStatus(Color.RED, e.getMessage());
        }
    }

    private void setLabelStatus(Color color, String message) {
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
