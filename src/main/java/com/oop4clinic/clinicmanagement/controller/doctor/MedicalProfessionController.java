package com.oop4clinic.clinicmanagement.controller.doctor;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.dto.PatientAppointmentInfoDto;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
import com.oop4clinic.clinicmanagement.util.SessionManager;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalProfessionController {

    @FXML private Pane mainContentPane;

    @FXML private Hyperlink appointmentsLink;
    @FXML private Hyperlink MedicalRecordInfo;
    @FXML private Hyperlink myProfileLink;

    @FXML private Label titleLabel;
    @FXML private DatePicker datePicker;
    @FXML private Button showDateButton;
    @FXML private Button showAllButton;
    @FXML private TextField searchAppointmentField;
    @FXML private Button searchAppointmentButton;
    @FXML private Button examineButton;

    @FXML private TableView<AppointmentDTO> appointmentsTable;
    @FXML private TableColumn<AppointmentDTO, String> colTime;
    @FXML private TableColumn<AppointmentDTO, String> colPatientName;
    @FXML private TableColumn<AppointmentDTO, String> colStatus;
    @FXML private TableColumn<AppointmentDTO, String> colReason;
    @FXML private Hyperlink patientInfo;
    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();
    private final ObservableList<AppointmentDTO> currentAppointmentList = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private Node[] originalChildren;


    int  doctorId = SessionManager.getLoggedUser();


    @FXML
    public void initialize() {
        setupTableColumns();
        datePicker.setValue(LocalDate.now());
        setupAppointmentRowClickListener();

        this.originalChildren = mainContentPane.getChildren().toArray(new Node[0]);
    }

    private void setupTableColumns() {
        colTime.setCellValueFactory(cellData -> {
            LocalDateTime startTime = cellData.getValue().getStartTime();
            String formattedTime = (startTime == null) ? "" : startTime.format(timeFormatter);
            return new ReadOnlyStringWrapper(formattedTime);
        });

        colPatientName.setCellValueFactory(cellData -> {
            AppointmentDTO appointment = cellData.getValue();
            PatientDTO patient = (appointment == null) ? null : appointment.getPatient();
            String patientName = (patient == null) ? "" : patient.getFullName();
            return new ReadOnlyStringWrapper(patientName);
        });

        colStatus.setCellValueFactory(cellData -> {
            AppointmentStatus status = cellData.getValue().getStatus();
            return new ReadOnlyStringWrapper(status == null ? "" : status.toString());
        });

        colReason.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getReason())
        );

        appointmentsTable.setItems(currentAppointmentList);
    }

    private void loadAppointmentsForDate(LocalDate date) {

        int loggedInDoctorId =   doctorId;
        try {

            List<AppointmentDTO> appointments = appointmentService.getAppointmentsForDoctorByDate(loggedInDoctorId, date);
            currentAppointmentList.setAll(appointments);

            if (date.equals(LocalDate.now())) {
                titleLabel.setText("Lịch hẹn hôm nay");
            } else {
                titleLabel.setText("Lịch hẹn ngày " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            datePicker.setDisable(false);
            searchAppointmentField.clear();

        } catch (Exception e) {
            System.err.println("Error loading appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowForDate(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            loadAppointmentsForDate(selectedDate);
        } else {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn ngày", "Vui lòng chọn một ngày để xem.");
        }
    }

    @FXML
    private void handleShowAll(ActionEvent event) {

        try {

            List<AppointmentDTO> allAppointments = appointmentService.getAllAppointmentsForDoctor(doctorId );
            currentAppointmentList.setAll(allAppointments);

            titleLabel.setText("Toàn bộ lịch sử khám");
            datePicker.setDisable(true);
            searchAppointmentField.clear();
        } catch (Exception e) {
            System.err.println("Error loading all appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchAppointment(ActionEvent event) {
        String keyword = searchAppointmentField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            appointmentsTable.setItems(currentAppointmentList);
            return;
        }

        ObservableList<AppointmentDTO> filteredList = currentAppointmentList.stream()
                .filter(appt -> appt.getPatient() != null &&
                        appt.getPatient().getFullName().toLowerCase().contains(keyword))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        appointmentsTable.setItems(filteredList);
    }

    @FXML
    private void handleShowPatientInfo(ActionEvent event) {

        try {

            List<AppointmentDTO> allDoctorAppointments = appointmentService.getAllAppointmentsForDoctor(doctorId);

            List<PatientAppointmentInfoDto> patientAppointmentList = allDoctorAppointments.stream()
                    .filter(appt -> appt != null && appt.getPatient() != null)
                    .collect(Collectors.groupingBy(
                            AppointmentDTO::getPatientId,
                            Collectors.maxBy(Comparator.comparing(AppointmentDTO::getStartTime))
                    ))
                    .values()
                    .stream()
                    .map(opt -> opt.orElse(null))
                    .filter(appt -> appt != null)
                    .map(appt -> new PatientAppointmentInfoDto(appt.getPatient(), appt))
                    .collect(Collectors.toList());


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/PatientInAppointment.fxml"));
            Node patientInfoView = loader.load();
            PatientInfoController patientController = loader.getController();

            patientController.populatePatientAppointments(patientAppointmentList);

            switchView(patientInfoView);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Tải Giao Diện", "Không thể tải giao diện thông tin bệnh nhân: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Không Xác Định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }



    @FXML
    private void handleShowMedicalRecordInfo(ActionEvent event) {

        try {

            List<MedicalRecordDTO> medicalRecords = medicalRecordService.getMedicalRecordsForDoctor(doctorId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecordInAppointment.fxml"));
            Node medicalRecordView = loader.load();

            MedicalRecordInfoController recordController = loader.getController();

            recordController.populateMedicalRecords(medicalRecords);

            switchView(medicalRecordView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Tải Giao Diện", "Không thể tải giao diện hồ sơ bệnh án: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Không Xác Định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowAppointments(ActionEvent event) {

        mainContentPane.getChildren().setAll(originalChildren);

        appointmentsTable.setItems(currentAppointmentList);

        datePicker.setValue(LocalDate.now());
        loadAppointmentsForDate(LocalDate.now());
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

    private void switchView(Node view) {
        if (mainContentPane instanceof BorderPane) {
            ((BorderPane) mainContentPane).setCenter(view);
        } else {

            mainContentPane.getChildren().setAll(view);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleExamine(ActionEvent event) {

        AppointmentDTO selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn một lịch hẹn để khám.");
            return;
        }
        if (selectedAppointment.getStatus() == AppointmentStatus.COMPLETED) {
            showAlert(Alert.AlertType.INFORMATION, "Đã hoàn tất", "Phiên khám này đã hoàn tất. Đang mở hồ sơ ở chế độ chỉ xem.");
        }
        MedicalRecordDTO record = null;
        try {
            record = medicalRecordService.findByAppointmentId(selectedAppointment.getId());
        } catch (Exception e) {
            if (!(e.getCause() instanceof jakarta.persistence.NoResultException)) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi cơ sở dữ liệu", "Không thể tìm hồ sơ: " + e.getMessage());
                return;
            }
        }
        openMedicalRecordDialog(selectedAppointment, record);
    }

    private void openMedicalRecordDialog(AppointmentDTO appointment, MedicalRecordDTO record) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/AddMedicalRecordDialog.fxml"));
            VBox dialogVBox = loader.load();

            AddMedicalRecordDialogController controller = loader.getController();

            controller.setData(appointment, record);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle(record == null ? "Tạo hồ sơ bệnh án" : "Cập nhật hồ sơ bệnh án");
            dialog.getDialogPane().setContent(dialogVBox);

            dialog.showAndWait();

            AddMedicalRecordDialogController closedController = loader.getController();
            MedicalRecordDTO savedRecord = closedController.getSavedRecord();

            if (savedRecord != null && closedController.isCompleted()) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công",
                        "Đã lưu hồ sơ và hoàn tất phiên khám cho bệnh nhân: " + appointment.getPatient().getFullName());

                if(datePicker.isDisabled()) {
                    handleShowAll(null);
                } else {
                    loadAppointmentsForDate(datePicker.getValue());
                }
            } else if (savedRecord != null) {
                showAlert(Alert.AlertType.INFORMATION, "Đã lưu",
                        "Đã lưu nháp hồ sơ. Phiên khám chưa hoàn tất.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tải giao diện", "Không thể mở cửa sổ hồ sơ: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi không xác định", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    private void openPatientDetailDialog(AppointmentDTO appointment) {

        PatientDTO patient = (appointment != null) ? appointment.getPatient() : null;
        if (patient == null) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Không có thông tin bệnh nhân cho lịch hẹn này.");
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

    private void setupAppointmentRowClickListener() {

        appointmentsTable.setRowFactory(tv -> {
            TableRow<AppointmentDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AppointmentDTO selectedAppointment = row.getItem();
                    openPatientDetailDialog(selectedAppointment);
                }
            });
            return row;
        });
    }
}