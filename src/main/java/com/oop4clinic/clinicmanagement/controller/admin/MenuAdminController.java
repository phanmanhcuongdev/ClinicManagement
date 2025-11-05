package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Controller chính cho màn Admin (MenuAdmin.fxml).
 * - Sidebar gọi openPatients(), openDoctors(), ...
 * - Center mặc định là dashboard
 * - Dashboard có thể reload runtime qua refreshDashboard()
 */
public class MenuAdminController {

    // ================== ROOT LAYOUT ==================
    @FXML
    private BorderPane root;

    // ========================================================
    //  INIT
    // ========================================================
    @FXML
    private void initialize() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DashBoard.fxml");
    }

    // ========================================================
    //  SIDEBAR ACTIONS
    // ========================================================

    @FXML
    private void openDashBoard() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DashBoard.fxml");
    }

    @FXML
    private void openPatients() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/PatientManagement.fxml");
    }

    @FXML
    private void openDoctors() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DoctorManagement.fxml");
    }

    @FXML
    private void openDepartments() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DepartmentManagement.fxml");
    }

    @FXML
    private void openAppointments() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/AppointmentManagement.fxml");
    }

    @FXML
    private void openRecords() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/MedicalRecordManagement.fxml");
    }

    @FXML
    private void openInvoices() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/InvoiceManagement.fxml");
    }

    @FXML
    private void handleExit(){
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

    // ========================================================
    //  HELPER: LOAD VIEW VÀO CENTER
    // ========================================================
    private void loadCenterView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            root.setCenter(view);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
