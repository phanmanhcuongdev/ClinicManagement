package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AppointmentPatientController {

    @FXML private TextField namePatient;
    @FXML private DatePicker time;
    @FXML private ComboBox<String> boxStatus;
    @FXML private TableView<AppointmentDTO> table;

    @FXML private TableColumn<AppointmentDTO, String> colTT;
    @FXML private TableColumn<AppointmentDTO, String> colTime;
    @FXML private TableColumn<AppointmentDTO, String> colDate;
    @FXML private TableColumn<AppointmentDTO, String> colDepartment;
    @FXML private TableColumn<AppointmentDTO, String> colDoctor;
    @FXML private TableColumn<AppointmentDTO, String> colReason;
    @FXML private TableColumn<AppointmentDTO, AppointmentStatus> colStatus;

    private final ObservableList<AppointmentDTO> appointmentList = FXCollections.observableArrayList();
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        setupStatusComboBox();
        setupTableColumns();
        loadAllAppointments();
        addSearchListeners();
    }

    private String mapStatusToVietnamese(AppointmentStatus status) {
        if (status == null) return "Không rõ";
        return switch (status) {
            case PENDING -> "Sắp tới";
            case CONFIRMED -> "Đã xác nhận";
            case CANCELED -> "Hủy hẹn";
            case COMPLETED -> "Hoàn thành";
        };
    }

    private AppointmentStatus mapVietnameseToStatus(String vietnameseStatus) {
        return switch (vietnameseStatus) {
            case "Sắp tới" -> AppointmentStatus.PENDING;
            case "Đã xác nhận" -> AppointmentStatus.CONFIRMED;
            case "Hủy hẹn" -> AppointmentStatus.CANCELED;
            case "Hoàn thành" -> AppointmentStatus.COMPLETED;
            default -> null;
        };
    }

    private void setupStatusComboBox() {
        List<String> statuses = Arrays.stream(AppointmentStatus.values())
                .map(this::mapStatusToVietnamese)
                .toList();
        boxStatus.getItems().addAll(statuses);
        boxStatus.getItems().add(0, "Trạng thái");
        boxStatus.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        colTime.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStartTime() != null
                        ? c.getValue().getStartTime().format(TIME_FORMAT)
                        : "")
        );

        colDate.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStartTime() != null
                        ? c.getValue().getStartTime().format(DATE_FORMAT)
                        : "")
        );

        colDepartment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartmentName()));
        colDoctor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctorName()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colStatus.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStatus()));

        colStatus.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>();
            private final List<String> updatableStatuses =
                    List.of(mapStatusToVietnamese(AppointmentStatus.CANCELED));

            {
                comboBox.getItems().addAll(updatableStatuses);
                comboBox.setOnAction(e -> {
                    AppointmentDTO dto = getTableView().getItems().get(getIndex());
                    if (dto != null) {
                        String vietStatus = comboBox.getValue();
                        AppointmentStatus newStatus = mapVietnameseToStatus(vietStatus);

                        if (newStatus == AppointmentStatus.CANCELED &&
                                (dto.getStatus() == AppointmentStatus.PENDING || dto.getStatus() == AppointmentStatus.CONFIRMED)) {
                            try {
                                AppointmentDTO updated = appointmentService.updateStatus(dto.getId(), newStatus);
                                dto.setStatus(updated.getStatus());
                                getTableView().refresh();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                new Alert(Alert.AlertType.ERROR, "Lỗi cập nhật trạng thái: " + ex.getMessage()).showAndWait();
                                comboBox.setValue(mapStatusToVietnamese(dto.getStatus()));
                            }
                        } else {
                            new Alert(Alert.AlertType.WARNING, "Bạn chỉ được phép Hủy lịch hẹn!").showAndWait();
                            comboBox.setValue(mapStatusToVietnamese(dto.getStatus()));
                        }
                    }
                });
            }

            @Override
            protected void updateItem(AppointmentStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String vietStatus = mapStatusToVietnamese(status);
                    if (status == AppointmentStatus.PENDING || status == AppointmentStatus.CONFIRMED) {
                        comboBox.setValue(vietStatus);
                        setGraphic(comboBox);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(vietStatus);
                    }
                }
            }
        });
    }

    private void loadAllAppointments() {
        try {
            PatientDTO currentPatient = UserSession.getCurrentPatient();
            if (currentPatient == null)
                throw new IllegalStateException("Không có bệnh nhân nào đang đăng nhập!");

            int patientId = currentPatient.getId();
            List<AppointmentDTO> data = appointmentService.findAppointmentsByPatientId(patientId);

            appointmentList.setAll(data);
            table.setItems(appointmentList);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu lịch hẹn!").showAndWait();
        }
    }

    @FXML
    private void loadAppointments() {
        String doctorName = namePatient.getText() == null ? "" : namePatient.getText().trim();
        String vietStatus = boxStatus.getValue();
        AppointmentStatus status = "Trạng thái".equals(vietStatus) ? null : mapVietnameseToStatus(vietStatus);
        LocalDate date = time.getValue();

        try {
            PatientDTO currentPatient = UserSession.getCurrentPatient();
            if (currentPatient == null)
                throw new IllegalStateException("Không có bệnh nhân nào đang đăng nhập!");

            int patientId = currentPatient.getId();
            List<AppointmentDTO> data = appointmentService.searchAppointmentsByPatient(patientId, doctorName, status, date);

            appointmentList.setAll(data);
            table.setItems(appointmentList);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi tìm kiếm lịch hẹn!").showAndWait();
        }
    }

    private void addSearchListeners() {
        namePatient.textProperty().addListener((obs, o, n) -> loadAppointments());
        boxStatus.valueProperty().addListener((obs, o, n) -> loadAppointments());
        time.valueProperty().addListener((obs, o, n) -> loadAppointments());
    }

    // ====== CHUYỂN MÀN HÌNH ======
    @FXML void handleInfo(ActionEvent e) throws IOException { switchScene("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"); }
    @FXML void handleRecord(ActionEvent e) throws IOException { switchScene("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"); }
    @FXML void handleBill(ActionEvent e) throws IOException { switchScene("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"); }
    @FXML void handleAppointment(ActionEvent e) throws IOException { switchScene("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"); }
    @FXML void handleLogout(ActionEvent e) throws IOException { switchScene("/com/oop4clinic/clinicmanagement/fxml/Login.fxml"); }
    @FXML void home(ActionEvent e) throws IOException { switchScene("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"); }

    private void switchScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
}
