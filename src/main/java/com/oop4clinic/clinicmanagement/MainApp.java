package com.oop4clinic.clinicmanagement;

import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
                // getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuPatient.fxml")
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuAdmin.fxml")
                //getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml")
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



