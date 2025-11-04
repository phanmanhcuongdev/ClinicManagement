package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.PatientAppointmentInfoDto;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


public class PatientInfoController {
    @FXML
    private TableView<PatientAppointmentInfoDto> patientsTable;

    @FXML
    private TableColumn<PatientAppointmentInfoDto, String> colName;

    @FXML
    private TableColumn<PatientAppointmentInfoDto, String> colGender;

    @FXML
    private TableColumn<PatientAppointmentInfoDto, String> colBirthday;

    @FXML
    private TableColumn<PatientAppointmentInfoDto, String> colPhone;

    @FXML
    private TableColumn<PatientAppointmentInfoDto, String> colStatus;

    @FXML
    private TableColumn<PatientAppointmentInfoDto, String> colTime;
    @FXML
    private TextField searchPatientField;
    private final ObservableList<PatientAppointmentInfoDto> allPatientAppointments = FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        setupPatientTableColumns();
        setupRowClickListener();
    }

    @FXML
    private Pane patientContentPane;

    private void setupPatientTableColumns() {
        colName.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPatientFullName())
        );
        colGender.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(genderVi(cellData.getValue().getPatientGender()))
        );
        colBirthday.setCellValueFactory(cellData -> {
            LocalDate dob = cellData.getValue().getPatientDateOfBirth();
            String formattedDate = (dob == null) ? "" : dob.format(dateFormatter);
            return new ReadOnlyStringWrapper(formattedDate);
        });
        colPhone.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPatientPhone())
        );
        colStatus.setCellValueFactory(cellData -> {
            AppointmentStatus status = cellData.getValue().getAppointmentStatus();
            return new ReadOnlyStringWrapper(status == null ? "" : status.toString());
        });
        colTime.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getAppointmentTime();
            String formattedTime = (time == null) ? "" : time.format(timeFormatter);
            return new ReadOnlyStringWrapper(formattedTime);
        });
    }

    public void populatePatientAppointments(List<PatientAppointmentInfoDto> patientAppointments) {
        allPatientAppointments.setAll(patientAppointments);
        patientsTable.setItems(allPatientAppointments);
    }

    @FXML
    private void handleSearchPatient(ActionEvent event) {
        String keyword = searchPatientField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            patientsTable.setItems(allPatientAppointments);
            return;
        }

        ObservableList<PatientAppointmentInfoDto> filteredList = allPatientAppointments.stream()
                .filter(p -> p.getPatientFullName().toLowerCase().contains(keyword) ||
                        (p.getPatientPhone() != null && p.getPatientPhone().contains(keyword)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        patientsTable.setItems(filteredList);
    }

    private static String genderVi(Gender g) {
        if (g == null) return "";
        return switch (g) {
            case MALE -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER -> "Khác";
        };
    }

    @FXML
    private void handleShowAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml"));
            Node view = loader.load();
            switchView(view);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải giao diện",
                    "Không thể tải giao diện lịch hẹn: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowPatientInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/PatientInAppointment.fxml"));
            Node view = loader.load();
            switchView(view);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải giao diện",
                    "Không thể tải giao diện thông tin bệnh nhân: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowMedicalRecordInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/MedicalRecordInAppointment.fxml"));
            Node view = loader.load();
            switchView(view);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải giao diện",
                    "Không thể tải giao diện hồ sơ bệnh án: " + e.getMessage());
        }
    }

    private void switchView(Node view) {
        if (patientContentPane instanceof BorderPane) {
            ((BorderPane) patientContentPane).setCenter(view);
        } else {
            patientContentPane.getChildren().setAll(view);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openPatientDetailDialog(PatientDTO patient) {
        if (patient == null) return;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/PatientDetailDialog.fxml"));
            Parent root = loader.load();

            PatientDetailDialogController controller = loader.getController();
            controller.setData(patient);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Chi tiết bệnh nhân: " + patient.getFullName());
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Tải Giao Diện", "Không thể mở cửa sổ chi tiết: " + e.getMessage());
        }
    }

    private void setupRowClickListener() {
        patientsTable.setRowFactory(tv -> {
            TableRow<PatientAppointmentInfoDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    PatientAppointmentInfoDto rowData = row.getItem();

                    PatientDTO selectedPatient = new PatientDTO();

                    selectedPatient.setId(rowData.getPatientId());
                    selectedPatient.setFullName(rowData.getPatientFullName());
                    selectedPatient.setGender(rowData.getPatientGender());
                    selectedPatient.setDateOfBirth(rowData.getPatientDateOfBirth());
                    selectedPatient.setPhone(rowData.getPatientPhone());

                    openPatientDetailDialog(selectedPatient);
                }
            });
            return row;
        });
    }
}