package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.entity.MedicalRecord;
import com.oop4clinic.clinicmanagement.model.mapper.MedicalRecordMapper;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
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
    @FXML private TableView<MedicalRecordDTO> recordTable;
    @FXML private TableColumn<MedicalRecordDTO, Integer> recordIdCol;
    @FXML private TableColumn<MedicalRecordDTO, String> patientNameCol;
    @FXML private TableColumn<MedicalRecordDTO, String> doctorNameCol;
    @FXML private TableColumn<MedicalRecordDTO, LocalDateTime> createdAtCol;
    @FXML private TableColumn<MedicalRecordDTO, String> diagnosisCol;
    @FXML private TableColumn<MedicalRecordDTO, String> symptomsCol;
    @FXML private TableColumn<MedicalRecordDTO, Void> editCol;
    @FXML private TextField searchField;
    @FXML private Button refreshButton;

    // ====== Data & Services ======
    private final MedicalRecordServiceImpl medicalRecordService = new MedicalRecordServiceImpl();
    private final MedicalRecordMapper medicalRecordMapper = new MedicalRecordMapper();
    private final ObservableList<MedicalRecordDTO> masterRecordList = FXCollections.observableArrayList();
    private FilteredList<MedicalRecordDTO> filteredData;

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
        patientNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPatientName()));
        doctorNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDoctorName()));
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
                    MedicalRecordDTO recordDTO = getTableView().getItems().get(getIndex());
                    handleEditAction(recordDTO);
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

        SortedList<MedicalRecordDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(recordTable.comparatorProperty());

        recordTable.setItems(sortedData);
    }

    private void applyFilters() {
        String searchText = searchField.getText();

        filteredData.setPredicate(record -> {
            if (searchText == null || searchText.isBlank()) return true;

            String keyword = searchText.toLowerCase().trim();
            return containsIgnoreCase(record.getPatientName(), keyword)
                    || containsIgnoreCase(record.getDoctorName(), keyword)
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
        List<MedicalRecordDTO> recordDTOs = medicalRecordService.getAll();
        masterRecordList.setAll(recordDTOs != null ? recordDTOs : FXCollections.observableArrayList());
    }

    @FXML
    private void handleRefreshAction(ActionEvent event) {
        searchField.clear();
        loadMedicalRecords();
    }

    // ====== Edit Dialog ======
    private void handleEditAction(MedicalRecordDTO recordToEdit) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/EditRecordDialog.fxml"));
            Parent root = loader.load();

            EditMedicalRecordController editController = loader.getController();
            editController.setMedicalRecordToEdit(recordToEdit);

            Stage stage = new Stage();
            stage.setTitle("Chỉnh Sửa Hồ Sơ Bệnh Án #" + recordToEdit.getId());
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
    private void showError(String message, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message + "\n" + e.getMessage());
        alert.showAndWait();
    }
}
