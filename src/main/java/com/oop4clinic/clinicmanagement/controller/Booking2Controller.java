package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.dao.DepartmentPatientDAO;
import com.oop4clinic.clinicmanagement.model.Department;

import com.oop4clinic.clinicmanagement.dao.AppointmentPatientDAO;
import com.oop4clinic.clinicmanagement.dao.DoctorPatientDAO;
import com.oop4clinic.clinicmanagement.dao.DoctorSchedulePatientDAO;
import com.oop4clinic.clinicmanagement.model.Doctor;
import com.oop4clinic.clinicmanagement.model.DoctorSchedule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.Cursor;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Booking2Controller {
    @FXML private Button homeButton;
    @FXML private MenuButton userMenuButton;
    @FXML private MenuItem infoPatient;
    @FXML private MenuItem recordPatient;
    @FXML private MenuItem billPatient;
    @FXML private MenuItem appointmentPatient;
    @FXML private MenuItem logoutPatient;

    @FXML private Button book1;
    @FXML private Button book2;

    @FXML private TextField symptom;
    @FXML private TextField department;
    @FXML private DatePicker date;

    @FXML private Button saveButton;
    @FXML private VBox departmentVBox;

    // DAO
    private final DoctorPatientDAO doctorDAO = new DoctorPatientDAO();
    private final DoctorSchedulePatientDAO scheduleDAO = new DoctorSchedulePatientDAO();
    private final AppointmentPatientDAO appointmentDAO = new AppointmentPatientDAO();
    private final DepartmentPatientDAO departmentDAO = new DepartmentPatientDAO();

    // định dạng ngày
    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter displayDayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final Locale vietnameseLocale = new Locale("vi", "VN");

    private Doctor selectedDoctorForBooking = null;
    private DoctorSchedule selectedScheduleForBooking = null;

    @FXML
    public void initialize() {
        department.textProperty().addListener((obs, oldText, newText) -> updateDepartmentVBox());
        date.valueProperty().addListener((obs, oldDate, newDate) -> updateDepartmentVBox());
        updateDepartmentVBox();
    }

    // lưu lịch hẹn
    @FXML
    void saveTime(ActionEvent event) {
        String patientSymptom = symptom.getText();
        if (patientSymptom == null || patientSymptom.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập triệu chứng của bạn.");
            symptom.requestFocus();
            return;
        }
        if (selectedDoctorForBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lịch", "Vui lòng chọn một Khoa.");
            return;
        }
        if (selectedScheduleForBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lịch", "Bạn đã chọn ngày, vui lòng chọn Giờ Khám.");
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận đặt lịch");
        confirmAlert.setHeaderText("Bạn có chắc muốn đặt lịch hẹn này?");
        confirmAlert.setContentText(String.format(
                "Triệu chứng: %s\nKhoa: %s\nBác sĩ: %s\nNgày: %s\nGiờ: %s",
                patientSymptom,
                selectedDoctorForBooking.getDepartmentName(),
                selectedDoctorForBooking.getFullName(),
                selectedScheduleForBooking.getWorkDate(),
                selectedScheduleForBooking.getWorkTime()
        ));
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int currentPatientId = 1;
                int departmentId = selectedDoctorForBooking.getDepartmentId();
                int doctorId = selectedDoctorForBooking.getId();
                String apptDate = selectedScheduleForBooking.getWorkDate();
                String apptTime = selectedScheduleForBooking.getWorkTime();
                String reason = patientSymptom;

                appointmentDAO.createAppointment(
                        currentPatientId,
                        departmentId,
                        doctorId,
                        apptDate,
                        apptTime,
                        reason
                );
                scheduleDAO.updateScheduleStatus(selectedScheduleForBooking.getId(), "OFF");
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đặt lịch thành công!");
                updateDepartmentVBox();
                selectedDoctorForBooking = null;
                selectedScheduleForBooking = null;
                symptom.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể lưu lịch hẹn. Vui lòng thử lại.");
            }
        }
    }

    // hiển thị danh sách khoa
    private void updateDepartmentVBox() {
        departmentVBox.getChildren().clear();

        String deptQuery = department.getText();
        LocalDate selectedDate = date.getValue();
        String dateStringQuery = null;
        if (selectedDate != null) {
            dateStringQuery = selectedDate.format(dbDateFormatter);
        }

        try {
            List<Department> filteredDepartments = departmentDAO.getFilteredDepartments(deptQuery, dateStringQuery);

            for (Department dept : filteredDepartments) {
                List<DoctorSchedule> schedules = scheduleDAO.getSchedulesByDepartment(dept.getId());
                List<DoctorSchedule> schedulesForCard = schedules;
                if (dateStringQuery != null) {
                    final String finalDateQuery = dateStringQuery;
                    schedulesForCard = schedules.stream()
                            .filter(s -> s.getWorkDate().equals(finalDateQuery))
                            .collect(Collectors.toList());
                }

                if (!schedulesForCard.isEmpty()) {
                    VBox departmentCard = createDepartmentCard(dept, schedulesForCard);
                    departmentVBox.getChildren().add(departmentCard);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", "Không thể tải danh sách khoa theo bộ lọc.");
        }
    }

    // tạo card khoa
    private VBox createDepartmentCard(Department dept, List<DoctorSchedule> schedules) {
        VBox card = new VBox();
        card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #dcdcdc;
            -fx-border-width: 1;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.2, 0, 4);
        """);

        VBox departmentInfoBox = new VBox(5);
        departmentInfoBox.setCursor(Cursor.HAND);

        Label nameLabel = new Label(dept.getName());
        nameLabel.setFont(new Font("System Bold", 16));
        departmentInfoBox.getChildren().add(nameLabel);

        VBox scheduleContainer = new VBox(10);
        scheduleContainer.setStyle("-fx-padding: 15 0 0 0;");

        // tạo thanh ngày
        HBox dateStrip = new HBox(10);
        ScrollPane dateScrollPane = new ScrollPane(dateStrip);
        dateScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        dateScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dateScrollPane.setPrefHeight(80);

        // tạo vùng chứa giờ (bên dưới thanh ngày)
        FlowPane timeSlotsPane = new FlowPane(10, 10);

        // populate ngày, khi click ngày sẽ đổ giờ vào timeSlotsPane
        ToggleGroup timeToggleGroup = new ToggleGroup();
        populateDateStrip(dateStrip, schedules, timeSlotsPane, timeToggleGroup);

        scheduleContainer.getChildren().addAll(new Separator(), dateScrollPane, new Label("Chọn giờ khám:"), timeSlotsPane);

        // toggle hiển thị lịch (ngày + giờ)
        scheduleContainer.setVisible(false);
        scheduleContainer.setManaged(false);

        departmentInfoBox.setOnMouseClicked(event -> {
            boolean isVisible = scheduleContainer.isVisible();
            scheduleContainer.setVisible(!isVisible);
            scheduleContainer.setManaged(!isVisible);
        });

        card.getChildren().addAll(departmentInfoBox, scheduleContainer);
        return card;
    }

    // tạo các nút ngày, khi chọn ngày đổ giờ vào timeSlotsPane
    private void populateDateStrip(HBox dateStrip, List<DoctorSchedule> schedules,
                                   FlowPane timeSlotsPane, ToggleGroup timeToggleGroup) {
        dateStrip.getChildren().clear();

        Set<String> availableDates = schedules.stream()
                .filter(s -> "AVAILABLE".equalsIgnoreCase(s.getStatus()))
                .map(DoctorSchedule::getWorkDate)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 10; i++) {
            LocalDate date = today.plusDays(i);
            String dateStringDB = date.format(dbDateFormatter);
            String dayOfWeek = "Thứ " + (date.getDayOfWeek().getValue() + 1);
            if (date.getDayOfWeek().getValue() == 7) dayOfWeek = "Chủ nhật";
            String dayMonth = date.format(displayDayMonthFormatter);

            ToggleButton dateButton = new ToggleButton(dayOfWeek + "\n" + dayMonth);
            dateButton.getStyleClass().add("date-button");
            dateStrip.getChildren().add(dateButton);

            if (!availableDates.contains(dateStringDB)) {
                dateButton.setDisable(true);
            } else {
                // khi click vào ngày: populate timeSlotsPane (clear trước)
                dateButton.setOnAction(e -> {
                    // bỏ chọn schedule và bác sĩ trước khi load giờ mới
                    this.selectedScheduleForBooking = null;
                    this.selectedDoctorForBooking = null;
                    department.setText(""); // nếu muốn hiển thị tên khoa, bạn có thể set lại dept name ở caller

                    boolean wasVisible = !timeSlotsPane.getChildren().isEmpty() && dateButton.isSelected();
                    // Clear và refill: (always refill for selected date)
                    timeSlotsPane.getChildren().clear();
                    timeToggleGroup.getToggles().clear();

                    List<DoctorSchedule> slotsForDay = schedules.stream()
                            .filter(s -> Objects.equals(s.getWorkDate(), dateStringDB) &&
                                    "AVAILABLE".equalsIgnoreCase(s.getStatus()))
                            .sorted((s1, s2) -> s1.getWorkTime().compareTo(s2.getWorkTime()))
                            .toList();

                    for (DoctorSchedule schedule : slotsForDay) {
                        ToggleButton timeButton = new ToggleButton(schedule.getWorkTime());
                        timeButton.setUserData(schedule);
                        timeButton.setToggleGroup(timeToggleGroup);
                        timeButton.getStyleClass().add("time-slot-button");

                        timeButton.setOnAction(ev -> {
                            selectedScheduleForBooking = schedule;
                            try {
                                selectedDoctorForBooking = doctorDAO.getDoctorById(schedule.getDoctorId());
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin bác sĩ.");
                            }
                        });

                        timeSlotsPane.getChildren().add(timeButton);
                    }
                });
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // chuyển scene
    @FXML void handleInfo(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML void handleLogout(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML void handleBook1(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
