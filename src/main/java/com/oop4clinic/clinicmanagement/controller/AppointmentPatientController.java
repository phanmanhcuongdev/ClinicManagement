package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.dao.AppointmentPatientDAO;
import com.oop4clinic.clinicmanagement.model.Appointment;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AppointmentPatientController {

    @FXML private TextField namePatient;
    @FXML private TextField status;
    @FXML private DatePicker time;

    @FXML private TableView<Appointment> table;
    @FXML private TableColumn<Appointment, String> colTT;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colDepartment;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colReason;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private ComboBox<String> boxStatus;

    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private final AppointmentPatientDAO dao = new AppointmentPatientDAO();

    // Giả lập biến lưu user đang đăng nhập (bạn có thể set sau khi login)
    private static int currentPatientId = 1;
    public static void setCurrentPatientId(int id) {
        currentPatientId = id;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAllAppointments();
        addSearchListeners();
    }

    private void setupTableColumns() {
        colTT.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1));
            }
        });

        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartTime()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppointmentDate()));
        colDepartment.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartmentName()));
        colDoctor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoctorName()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));

        // hiển thị tiếng Việt trạng thái
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        // combo chỉnh trạng thái
        colStatus.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>();

            {
                comboBox.getItems().addAll("Sắp tới", "Hoàn thành", "Đã hủy");
                comboBox.setOnAction(e -> {
                    Appointment ap = getTableView().getItems().get(getIndex());
                    if (ap != null) {
                        String newStatus = comboBox.getValue();
                        ap.setStatus(newStatus);
                        AppointmentPatientDAO.updateStatus(ap.getId(), newStatus);
                    }
                });
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty) setGraphic(null);
                else {
                    comboBox.setValue(status == null || status.isEmpty() ? "Sắp tới" : status);
                    setGraphic(comboBox);
                }
            }
        });
    }

    // chỉ tải lịch hẹn của bệnh nhân đang đăng nhập
    private void loadAllAppointments() {
        appointmentList.clear();
        List<Appointment> data = AppointmentPatientDAO.getAllAppointmentsByPatient(currentPatientId);
        appointmentList.addAll(data);
        table.setItems(appointmentList);
        table.refresh();
    }

    // tìm kiếm có lọc theo bác sĩ, trạng thái, ngày — nhưng vẫn chỉ cho bệnh nhân đăng nhập
    private void loadAppointments() {
        String doctorName = namePatient.getText() == null ? "" : namePatient.getText().trim();
        String st = status.getText() == null ? "" : status.getText().trim();
        String date = (time.getValue() == null) ? "" : time.getValue().toString();

        appointmentList.clear();
        List<Appointment> data = AppointmentPatientDAO.getAppointmentsByPatient(currentPatientId, doctorName, st, date);
        appointmentList.addAll(data);
        table.setItems(appointmentList);
        table.refresh();
    }

    private void addSearchListeners() {
        namePatient.textProperty().addListener((obs, oldV, newV) -> loadAppointments());
        status.textProperty().addListener((obs, oldV, newV) -> loadAppointments());
        time.valueProperty().addListener((obs, oldV, newV) -> loadAppointments());
    }

    // chuyển trang
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Book1.fxml"));
        Scene scene = namePatient.getScene();
        scene.setRoot(root);
    }
}
