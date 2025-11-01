package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.dao.MedicalRecordDAO;
import com.oop4clinic.clinicmanagement.model.MedicalRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MedicalRecordController implements Initializable {
    @FXML private TableView<MedicalRecord> medicalRecordTable;
    @FXML private TableColumn<MedicalRecord, Integer> colTT;
    @FXML private TableColumn<MedicalRecord, String> colDate;
    @FXML private TableColumn<MedicalRecord, String> colDepartment;
    @FXML private TableColumn<MedicalRecord, String> colDiagnosis;
    @FXML private TableColumn<MedicalRecord, Void> colStatus;
    @FXML private Button homeButton;

    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadMedicalRecords();
    }

    private void setupTableColumns() {
        colTT.setCellValueFactory(new PropertyValueFactory<>("tt"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examinationDate"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        Callback<TableColumn<MedicalRecord, Void>, TableCell<MedicalRecord, Void>> cellFactory = col -> {
            return new TableCell<>() {
                private final Hyperlink viewLink = new Hyperlink("Xem chi tiết");
                {
                    viewLink.setOnAction(event -> {
                        MedicalRecord recordData = getTableView().getItems().get(getIndex());
                        openDetailWindow(recordData.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewLink);
                    }
                }
            };
        };

        colStatus.setCellFactory(cellFactory);
    }

    private void openDetailWindow(int recordId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecordDetail.fxml"));
            Parent root = loader.load();
            MedicalRecordDetailController detailController = loader.getController();
            detailController.loadRecordDetails(recordId);

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Hồ Sơ Bệnh Án");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Lỗi: Không thể tải file FXML cho cửa sổ chi tiết.");
            e.printStackTrace();
        }
    }

    private void loadMedicalRecords() {
        List<MedicalRecord> recordsList = medicalRecordDAO.getAllMedicalRecords();
        ObservableList<MedicalRecord> observableRecords = FXCollections.observableArrayList(recordsList);
        medicalRecordTable.setItems(observableRecords);
    }

    // chuyen trang
    @FXML void handleInfo(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Logout.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Book1.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
}