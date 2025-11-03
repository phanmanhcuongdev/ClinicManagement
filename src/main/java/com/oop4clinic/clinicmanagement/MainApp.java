package com.oop4clinic.clinicmanagement;

import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml")
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml")
        );
        init();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Clinic OOP4");
        stage.show();
    }

    @Override
    public void stop()
    {
        com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider.close();
    }

    @Override
    public void init() throws Exception {
        EntityManagerProvider.init();

    }
    public static void main(String[] args) {
        launch(args);
    }

}



