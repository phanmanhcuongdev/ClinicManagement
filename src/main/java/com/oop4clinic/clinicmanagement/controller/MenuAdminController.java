package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MenuAdminController implements Initializable {

    @FXML private BorderPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DashBoard.fxml");
    }

    @FXML
    void openDashBoard(ActionEvent event) {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DashBoard.fxml");
    }

    @FXML
    void openAppointments(ActionEvent event){
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/AppointmentManagement.fxml");
    }
    @FXML
    void openDepartments(ActionEvent event){

    }

    @FXML
    void openMedicalRecord(ActionEvent event){
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/MedicalRecordManagement.fxml");
    }

    @FXML
    void openSettings(ActionEvent event){

    }

    @FXML
    void openInvoice(ActionEvent event) {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/InvoiceManagement.fxml");
    }

    private void loadCenterView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            root.setCenter(view);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void handleExit(){
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApp.class.getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml")
            );

            Parent temproot = fxmlLoader.load();
            stage.setScene(new Scene(temproot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
