package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.MainApp;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.service.impl.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmpasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String phone = phoneField.getText();
        String pass = passwordField.getText();
        String confirmpass = confirmpasswordField.getText();

        AuthService authService = new AuthService();

        try {
            if(authService.register(phone, pass, confirmpass)){
                setLabelStatus(Color.GREEN, "Đăng ký thành công!");
            }
            else {
                setLabelStatus(Color.RED, "Đăng ký thất bại!");
            }

//            FXMLLoader loader = new FXMLLoader(
//                    MainApp.class.getResource("/com/oop4clinic/clinicmanagement/fxml/PatientDashboard.fxml")
//            );
//            Parent root = loader.load();
//            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Clinic OOP4 - Patient");
//            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            setLabelStatus(Color.RED, e.getMessage());
        }
    }

    private void setLabelStatus(Color color, String message) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
    }

    @FXML
    private void handleReturnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) phoneField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Clinic OOP4 - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
