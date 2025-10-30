package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import com.oop4clinic.clinicmanagement.services.MedicalRecordService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class MedicalRecordManagementController implements Initializable {

    // ====== FXML Components ======
    @FXML private TableView<MedicalRecord> recordTable;
    @FXML private TableColumn<MedicalRecord, Integer> recordIdCol;
    @FXML private TableColumn<MedicalRecord, String> patientNameCol;
    @FXML private TableColumn<MedicalRecord, String> doctorNameCol;
    @FXML private TableColumn<MedicalRecord, LocalDateTime> createdAtCol;
    @FXML private TableColumn<MedicalRecord, String> diagnosisCol;
    @FXML private TableColumn<MedicalRecord, String> symptomsCol;
    @FXML private TableColumn<MedicalRecord, Void> editCol;
    @FXML private TextField searchField;
    @FXML private Button refreshButton;

    // ====== Data & Services ======
    private final MedicalRecordService medicalRecordService = new MedicalRecordService();
    private final ObservableList<MedicalRecord> masterRecordList = FXCollections.observableArrayList();
    private FilteredList<MedicalRecord> filteredData;

    // ====== Initialize ======
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupFiltering();
        loadMedicalRecords();
    }

    // ====== Table Setup ======
    private void setupTableColumns() {
        recordIdCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        patientNameCol.setCellValueFactory(cell -> new SimpleStringProperty(getPatientNameSafe(cell.getValue())));
        doctorNameCol.setCellValueFactory(cell -> new SimpleStringProperty(getDoctorNameSafe(cell.getValue())));
        createdAtCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCreatedAt()));
        diagnosisCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDiagnosis()));
        symptomsCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSymptoms()));

        setupCreatedAtFormatter();
        setupEditButtonColumn();

        recordTable.setPlaceholder(new Label("Không có hồ sơ bệnh án nào."));
    }

    private void setupCreatedAtFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        createdAtCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });
    }

    private void setupEditButtonColumn() {
        editCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");

            {
                editButton.setOnAction(event -> {
                    MedicalRecord record = getTableView().getItems().get(getIndex());
                    handleEditAction(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    // ====== Filtering ======
    private void setupFiltering() {
        filteredData = new FilteredList<>(masterRecordList, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        SortedList<MedicalRecord> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(recordTable.comparatorProperty());

        recordTable.setItems(sortedData);
    }

    private void applyFilters() {
        String searchText = searchField.getText();

        filteredData.setPredicate(record -> {
            if (searchText == null || searchText.isBlank()) return true;

            String keyword = searchText.toLowerCase().trim();
            return getPatientNameSafe(record).toLowerCase().contains(keyword)
                    || getDoctorNameSafe(record).toLowerCase().contains(keyword)
                    || containsIgnoreCase(record.getDiagnosis(), keyword)
                    || containsIgnoreCase(record.getSymptoms(), keyword)
                    || String.valueOf(record.getId()).contains(keyword);
        });
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        return field != null && field.toLowerCase().contains(keyword);
    }

    // ====== Load Data ======
    private void loadMedicalRecords() {
        List<MedicalRecord> records = medicalRecordService.getAll();
        masterRecordList.setAll(records != null ? records : FXCollections.observableArrayList());
    }

    @FXML
    private void handleRefreshAction(ActionEvent event) {
        searchField.clear();
        loadMedicalRecords();
    }

    // ====== Edit Dialog ======
    private void handleEditAction(MedicalRecord record) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/EditRecordDialog.fxml"));
            Parent root = loader.load();

            EditMedicalRecordController editController = loader.getController();
            editController.setMedicalRecordToEdit(record);

            Stage stage = new Stage();
            stage.setTitle("Chỉnh Sửa Hồ Sơ Bệnh Án #" + record.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(recordTable.getScene().getWindow());
            stage.showAndWait();

            loadMedicalRecords();
        } catch (IOException e) {
            showError("Không thể mở màn hình chỉnh sửa.", e);
        } catch (Exception e) {
            showError("Đã xảy ra lỗi không mong muốn.", e);
        }
    }

    // ====== Helper Methods ======
    private String getPatientNameSafe(MedicalRecord record) {
        return (record != null && record.getPatient() != null)
                ? record.getPatient().getFullName()
                : "N/A";
    }

    private String getDoctorNameSafe(MedicalRecord record) {
        return (record != null && record.getDoctor() != null)
                ? record.getDoctor().getFullName()
                : "N/A";
    }

    private void showError(String message, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}
