package com.oop4clinic.clinicmanagement.controller;

import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.layout.*;

public class MenuAdminController {
    @FXML
    private BorderPane root;

    @FXML
    private void openDoctors() {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/DoctorManagement.fxml"));
            root.setCenter(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void openPatients()
    {
        try {
            Node view = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/PatientManagement.fxml"));
            root.setCenter(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
