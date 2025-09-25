package com.oop4clinic.clinicmanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Xử lý đăng nhập
    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if ("admin".equals(user) && "123".equals(pass)) {
            System.out.println("Đăng nhập thành công!");
        } else {
            System.out.println("Sai tài khoản hoặc mật khẩu!");
        }
    }

    // Register.fxml
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
