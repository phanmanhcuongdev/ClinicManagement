package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.util.SessionManager;
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

    private static int currentPatientId = SessionManager.getLoggedUser();;
    public static void setCurrentPatientId(int id) {
        currentPatientId = id;
    }

    @FXML
    public void initialize() {
        setupStatusComboBox();
        setupTableColumns();
        loadAllAppointments();
        addSearchListeners();
    }

    // chuyen tu tieng anh sang tieng viet
    private String mapStatusToVietnamese(AppointmentStatus status) {
        if (status == null) return "Không rõ";
        return switch (status) {
            case PENDING -> "Sắp tới";
            case CONFIRMED -> "Đã xác nhận";
            case CANCELED -> "Hủy hẹn";
            case COMPLETED -> "Hoàn thành";
        };
    }

    // chuyen tu tieng viet sang tieng anh
    private AppointmentStatus mapVietnameseToStatus(String vietnameseStatus) {
        return switch (vietnameseStatus) {
            case "Sắp tới" -> AppointmentStatus.PENDING;
            case "Hủy hẹn" -> AppointmentStatus.CANCELED;
            case "Đã xác nhận" -> AppointmentStatus.CONFIRMED;
            case "Hoàn thành" -> AppointmentStatus.COMPLETED;
            default -> null;
        };
    }

    private void setupStatusComboBox() {
        List<String> statuses = Arrays.stream(AppointmentStatus.values()).map(this::mapStatusToVietnamese).toList();
        boxStatus.getItems().addAll(statuses);
        boxStatus.getItems().add(0, "Trạng thái");
        boxStatus.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        // so thu tu
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });

        // mapping tu dto
        colTime.setCellValueFactory(c -> {
            if (c.getValue().getStartTime() != null) {
                return new SimpleStringProperty(c.getValue().getStartTime().format(TIME_FORMAT));
            }
            return new SimpleStringProperty("");
        });
        colDate.setCellValueFactory(c -> {
            if (c.getValue().getStartTime() != null) {
                return new SimpleStringProperty(c.getValue().getStartTime().format(DATE_FORMAT));
            }
            return new SimpleStringProperty("");
        });
        colDepartment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartmentName()));
        colDoctor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctorName()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));

        // cot trang thai
        colStatus.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStatus()));

        colStatus.setCellFactory(tc -> new TableCell<AppointmentDTO, AppointmentStatus>() {
            private final ComboBox<String> comboBox = new ComboBox<>();
            private final List<String> updatableStatuses = Arrays.asList(
                    mapStatusToVietnamese(AppointmentStatus.CANCELED) // Bệnh nhân chỉ được Hủy
            );

            {
                comboBox.getItems().addAll(updatableStatuses);

                comboBox.setOnAction(e -> {
                    AppointmentDTO dto = getTableView().getItems().get(getIndex());
                    if (dto != null) {
                        String newStatusViet = comboBox.getValue();
                        AppointmentStatus newStatus = mapVietnameseToStatus(newStatusViet);

                        // chi cho huy khi lich hen ở trang thai dang cho
                        if (newStatus == AppointmentStatus.CANCELED &&
                                (dto.getStatus() == AppointmentStatus.PENDING || dto.getStatus() == AppointmentStatus.CONFIRMED)) {

                            try {
                                AppointmentDTO updatedDto = appointmentService.updateStatus(dto.getId(), newStatus);
                                dto.setStatus(updatedDto.getStatus());
                                getTableView().refresh();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi cập nhật trạng thái! " + ex.getMessage());
                                alert.showAndWait();
                                // Reset ComboBox về trạng thái cũ nếu cập nhật thất bại
                                comboBox.setValue(mapStatusToVietnamese(dto.getStatus()));
                            }
                        } else {
                            // Cảnh báo nếu cố gắng set trạng thái không hợp lệ
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Bạn chỉ được phép Hủy lịch hẹn!");
                            alert.showAndWait();
                            comboBox.setValue(mapStatusToVietnamese(dto.getStatus())); // Reset
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

                    // Chỉ cho phép thay đổi (Hủy) nếu trạng thái là PENDING hoặc CONFIRMED
                    if (status == AppointmentStatus.PENDING || status == AppointmentStatus.CONFIRMED) {
                        comboBox.setValue(vietStatus);
                        setGraphic(comboBox);
                        setText(null);
                    } else {
                        // Hiển thị Label (chữ) cho trạng thái CANCELED/COMPLETED
                        setGraphic(null);
                        setText(vietStatus);
                    }
                }
            }
        });
    }

    private void loadAllAppointments() {
        try {
            List<AppointmentDTO> data = appointmentService.findAppointmentsByPatientId(currentPatientId);

            appointmentList.clear();
            appointmentList.addAll(data);
            table.setItems(appointmentList);
            table.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể tải dữ liệu lịch hẹn!");
            alert.showAndWait();
        }
    }

    @FXML
    private void loadAppointments() {
        String doctorName = namePatient.getText() == null ? "" : namePatient.getText().trim();
        String vietStatus = boxStatus.getValue();
        AppointmentStatus status = "Trạng thái".equals(vietStatus) ? null : mapVietnameseToStatus(vietStatus);
        LocalDate date = (time.getValue() == null) ? null : time.getValue();

        try {
            List<AppointmentDTO> data = appointmentService.searchAppointmentsByPatient(
                    currentPatientId,
                    doctorName,
                    status,
                    date
            );

            appointmentList.clear();
            appointmentList.addAll(data);
            table.setItems(appointmentList);
            table.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi tìm kiếm lịch hẹn!");
            alert.showAndWait();
        }
    }

    private void addSearchListeners() {
        namePatient.textProperty().addListener((obs, oldV, newV) -> loadAppointments());
        boxStatus.valueProperty().addListener((obs, oldV, newV) -> loadAppointments());
        time.valueProperty().addListener((obs, oldV, newV) -> loadAppointments());
    }

    @FXML void handleInfo(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Logout.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
}