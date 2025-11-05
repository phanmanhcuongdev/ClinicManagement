package com.oop4clinic.clinicmanagement.controller.doctor;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.dto.PatientAppointmentInfoDto;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.PatientServiceImpl;
import com.oop4clinic.clinicmanagement.util.SessionManager;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.event.ActionEvent;
import javafx.stage.Stage;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicalRecordInfoController {
    @FXML
    private TableView<MedicalRecordDTO> medicalRecordsTable;
    @FXML
    private TableColumn<MedicalRecordDTO, Integer> colRecordId;
    @FXML
    private TableColumn<MedicalRecordDTO, String> colRecordPatientName;
    @FXML
    private TableColumn<MedicalRecordDTO, String> colRecordSymptoms;
    @FXML
    private TableColumn<MedicalRecordDTO, String> colRecordDiagnosis;
    @FXML
    private TableColumn<MedicalRecordDTO, String> colRecordDate;
    @FXML
    private TableColumn<MedicalRecordDTO, String> colRecordPrescription;
    @FXML
    private TableColumn<MedicalRecordDTO, String> colRecordNotes;

    @FXML private TextField searchField;
    @FXML
    private Pane medicalRecordContentPane;
    private final PatientService patientService = new PatientServiceImpl();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();
    private final ObservableList<MedicalRecordDTO> medicalRecordList = FXCollections.observableArrayList();

    private MedicalRecordDTO selectedRecord;
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    int doctorId = SessionManager.getLoggedUser();


    @FXML
    public void initialize() {
        medicalRecordsTable.setItems(medicalRecordList);
        setupMedicalRecordTableColumns();
        loadMedicalRecords();
        setupRowSelection();
        setupMedicalRecordRowClickListener();
    }
    public void populateMedicalRecords(List<MedicalRecordDTO> records) {
        medicalRecordList.setAll(records);
    }
    private void setupMedicalRecordTableColumns() {
        colRecordId.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getId())
        );
        colRecordPatientName.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPatientName())
        );
        colRecordSymptoms.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getSymptoms())
        );
        colRecordDiagnosis.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDiagnosis())
        );
        colRecordPrescription.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPrescription())
        );
        colRecordNotes.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNotes())
        );
        colRecordDate.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getCreatedAt();
            String formatted = (dateTime == null) ? "" : dateTime.format(dateTimeFormatter);
            return new ReadOnlyStringWrapper(formatted);
        });
    }
    private void loadMedicalRecords() {
        try {
            List<MedicalRecordDTO> records = medicalRecordService.getMedicalRecordsForDoctor(doctorId);
            medicalRecordList.setAll(records);
            searchField.clear();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách hồ sơ: " + e.getMessage());
        }
    }
    @FXML
    private void handleSearchMedicalRecord(ActionEvent event) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadMedicalRecords();
            return;
        }
        try {
            List<MedicalRecordDTO> result = medicalRecordService.searchByPatientName(keyword);
            medicalRecordList.setAll(result);
            if (result.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không tìm thấy hồ sơ nào khớp với từ khóa.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteMedicalRecord(ActionEvent event) {
        if (selectedRecord == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn hồ sơ cần xóa!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Xác nhận xóa hồ sơ");
        confirm.setContentText("Bạn có chắc chắn muốn xóa hồ sơ (ID: " + selectedRecord.getId() + ") của bệnh nhân "
                + selectedRecord.getPatientName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = medicalRecordService.deleteMedicalRecord(selectedRecord.getId());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa hồ sơ thành công!");
                    loadMedicalRecords();
                    selectedRecord = null;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể xóa hồ sơ. Vui lòng thử lại.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private void setupRowSelection() {
        medicalRecordsTable.setOnMouseClicked((MouseEvent event) -> {
            selectedRecord = medicalRecordsTable.getSelectionModel().getSelectedItem();
        });
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

    @FXML
    private void handleShowPatientInfo(ActionEvent event) {
        try {
            List<AppointmentDTO> allAppointmentsToday = appointmentService.getAppointmentsForToday(doctorId);
            List<PatientAppointmentInfoDto> patientInfoList = allAppointmentsToday.stream()
                    .map(appointment -> new PatientAppointmentInfoDto(appointment.getPatient(), appointment))
                    .collect(Collectors.toList());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/PatientInAppointment.fxml"));
            Node view = loader.load();

            PatientInfoController patientController = loader.getController();
            patientController.populatePatientAppointments(patientInfoList);

            switchView(view);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải giao diện",
                    "Không thể tải giao diện thông tin bệnh nhân: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi không xác định",
                    "Đã xảy ra lỗi khi chuyển sang màn hình bệnh nhân: " + e.getMessage());
        }
    }

    private void switchView(Node view) {
        if (medicalRecordContentPane instanceof BorderPane) {
            ((BorderPane) medicalRecordContentPane).setCenter(view);
        } else {
            medicalRecordContentPane.getChildren().setAll(view);
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
        if (patient == null) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Không tìm thấy thông tin bệnh nhân (ID có thể đã bị xóa).");
            return;
        }
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

    private void setupMedicalRecordRowClickListener() {
        medicalRecordsTable.setRowFactory(tv -> {
            TableRow<MedicalRecordDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    MedicalRecordDTO selectedRecord = row.getItem();
                    if (selectedRecord == null || selectedRecord.getPatientId() == null) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Hồ sơ này bị thiếu ID bệnh nhân.");
                        return;
                    }
                    PatientDTO fullPatientDetails = patientService.getPatient(selectedRecord.getPatientId());
                    openPatientDetailDialog(fullPatientDetails);
                }
            });
            return row;
        });
    }

    @FXML
    private void handleShowMyProfile(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/DoctorProfile.fxml")
            );
            Parent root = loader.load();

            DoctorProfileController controller = loader.getController();

            User loggedInUser = UserSession.getCurrentUser();
            controller.setLoggedInDoctor(loggedInUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
