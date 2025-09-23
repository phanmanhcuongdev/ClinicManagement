package com.oop4clinic.clinicmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Clinic OOP4");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



