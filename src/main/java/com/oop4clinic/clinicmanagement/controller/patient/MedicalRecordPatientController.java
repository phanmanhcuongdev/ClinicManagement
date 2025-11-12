package com.oop4clinic.clinicmanagement.controller.patient;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
import com.oop4clinic.clinicmanagement.util.UserSession;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MedicalRecordPatientController implements Initializable {
    @FXML private TableView<MedicalRecordDTO> medicalRecordTable;
    @FXML private TableColumn<MedicalRecordDTO, Integer> colTT;
    @FXML private TableColumn<MedicalRecordDTO, LocalDateTime> colDate;
    @FXML private TableColumn<MedicalRecordDTO, String> colDepartment;
    @FXML private TableColumn<MedicalRecordDTO, String> colDiagnosis;
    @FXML private TableColumn<MedicalRecordDTO, Void> colStatus;
    @FXML private Button homeButton;

    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadMedicalRecords();
    }

    private void setupTableColumns() {
        // cot hien stt trong bang
        colTT.setCellFactory(column -> new TableCell<MedicalRecordDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colDate.setCellFactory(column -> new TableCell<MedicalRecordDTO, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        colDepartment.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        colDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        Callback<TableColumn<MedicalRecordDTO, Void>, TableCell<MedicalRecordDTO, Void>> cellFactory = col -> {
            return new TableCell<>() {
                // hien link chuyen sang trang moi
                private final Hyperlink viewLink = new Hyperlink("Xem chi tiết");
                {
                    viewLink.setOnAction(event -> {
                        MedicalRecordDTO recordData = getTableView().getItems().get(getIndex());
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
            Object controller = loader.getController();
            if (controller.getClass().getSimpleName().equals("MedicalRecordDetailController")) {
                ((MedicalRecordDetailController) controller).loadRecordDetails(recordId);
            }

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
        try {
            var currentPatient = UserSession.getCurrentPatient();

            if (currentPatient == null) {
                System.err.println("⚠ Không tìm thấy bệnh nhân trong session, không thể tải ho sơ!");
                return;
            }

            int patientId = currentPatient.getId();
            List<MedicalRecordDTO> recordsList = medicalRecordService.getByPatientId(patientId);

            if (recordsList != null && !recordsList.isEmpty()) {
                ObservableList<MedicalRecordDTO> observableRecords = FXCollections.observableArrayList(recordsList);
                medicalRecordTable.setItems(observableRecords);
            } else {
                medicalRecordTable.setItems(FXCollections.emptyObservableList());
                System.err.println("Không có hồ sơ bệnh án nào được tìm thấy cho bệnh nhân ID: " + patientId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            medicalRecordTable.setItems(FXCollections.emptyObservableList());
            System.err.println("Lỗi: Không thể tải danh sách hồ sơ bệnh án.");
        }
    }


    // chuyển trang
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml"));
        UserSession.clear();
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
}