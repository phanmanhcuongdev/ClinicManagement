package com.oop4clinic.clinicmanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField phoneField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String phone = phoneField.getText();
        String user = usernameField.getText();
        String pass = passwordField.getText();

        System.out.println("Đăng ký thành công: " + user + " - " + phone);
    }
}
