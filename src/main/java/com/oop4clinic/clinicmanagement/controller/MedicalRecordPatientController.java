package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
import com.oop4clinic.clinicmanagement.util.SessionManager;
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
    // --- KHAI BÁO CÁC THUỘC TÍNH FXM L ---
    @FXML private TableView<MedicalRecordDTO> medicalRecordTable;
    @FXML private TableColumn<MedicalRecordDTO, Integer> colTT;
    @FXML private TableColumn<MedicalRecordDTO, LocalDateTime> colDate;
    @FXML private TableColumn<MedicalRecordDTO, String> colDepartment;
    @FXML private TableColumn<MedicalRecordDTO, String> colDiagnosis;
    @FXML private TableColumn<MedicalRecordDTO, Void> colStatus;
    @FXML private Button homeButton;

    // Thay thế MedicalRecordDAO bằng MedicalRecordService
    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();

    // GIẢ ĐỊNH: ID của bệnh nhân đang đăng nhập
    // BẠN CẦN THAY THẾ NÀY BẰNG CƠ CHẾ LẤY ID THỰC TẾ (ví dụ: từ Session/Login Context)
    private static final int CURRENT_PATIENT_ID = SessionManager.getLoggedUser();;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadMedicalRecords();
    }

    private void setupTableColumns() {
        // Cột TT: Hiển thị số thứ tự
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

        // Cột Date: Hiển thị createdAt và định dạng lại
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

        // Cột Department: Giữ nguyên PropertyValueFactory
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        // Cột Diagnosis
        colDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        // Cột Status (Xem chi tiết)
        Callback<TableColumn<MedicalRecordDTO, Void>, TableCell<MedicalRecordDTO, Void>> cellFactory = col -> {
            return new TableCell<>() {
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
            // Lưu ý: Cần đảm bảo MedicalRecordDetailController đã được chỉnh sửa để dùng DTO
            // và đã được load thành công.
            Object controller = loader.getController();
            // Giả định controller có phương thức loadRecordDetails(int)
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
        // THAY ĐỔI LỚN NHẤT: Gọi phương thức getByPatientId thay vì getAll
        // Phương thức này chỉ trả về hồ sơ của bệnh nhân có ID là CURRENT_PATIENT_ID
        List<MedicalRecordDTO> recordsList = medicalRecordService.getByPatientId(CURRENT_PATIENT_ID);

        if (recordsList != null) {
            ObservableList<MedicalRecordDTO> observableRecords = FXCollections.observableArrayList(recordsList);
            medicalRecordTable.setItems(observableRecords);
        } else {
            medicalRecordTable.setItems(FXCollections.emptyObservableList());
            System.err.println("Lỗi: Không thể tải danh sách hồ sơ bệnh án.");
        }
    }

    // --- Xử lý chuyển trang ---
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