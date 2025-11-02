package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.service.impl.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private Label statusLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username  = usernameField.getText();
        String pass = passwordField.getText();

        AuthService authService = new AuthService();

        try {
            User user = authService.login(username, pass);
            
            setLabelStatus(Color.GREEN, "Đăng nhập thành công!");
            String url = "";
            if (user.getRole().equals(UserRole.PATIENT)){
                url =  "/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml";
            }

            if (user.getRole().equals(UserRole.DOCTOR)){
                url =  "/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml";
            }

            if (user.getRole().equals(UserRole.ADMIN)){
                url =  "/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml";
            }

            loadSence(url, user);


        } catch (Exception e) {
            setLabelStatus(Color.RED, e.getMessage());
        }
    }

    private void setLabelStatus(Color color, String message) {
        statusLabel.setTextFill(color);
        statusLabel.setText(message);
    }
    
    private void loadSence(String url, User loggedInUser) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Object controller = loader.getController();

        if (controller instanceof MedicalProfessionController medicalCtrl) {
            medicalCtrl.setLoggedInDoctor(loggedInUser);
        }

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
