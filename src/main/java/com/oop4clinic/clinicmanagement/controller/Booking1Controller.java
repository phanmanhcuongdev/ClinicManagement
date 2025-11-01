package com.oop4clinic.clinicmanagement.controller;

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
import java.util.*;
import java.util.stream.Collectors;

public class Booking1Controller {
    @FXML private Button homeButton;
    @FXML private MenuButton userMenuButton;
    @FXML private MenuItem infoPatient;
    @FXML private MenuItem recordPatient;
    @FXML private MenuItem billPatient;
    @FXML private MenuItem appointmentPatient;
    @FXML private MenuItem logoutPatient;

    @FXML private TextField nameDoctor;
    @FXML private TextField symptom;
    @FXML private TextField department;

    @FXML private Button book1;
    @FXML private Button Book2;
    @FXML private Button saveButton;
    @FXML private VBox doctorVBox;

    // DAO
    private final DoctorPatientDAO doctorDAO = new DoctorPatientDAO();
    private final DoctorSchedulePatientDAO scheduleDAO = new DoctorSchedulePatientDAO();
    private final AppointmentPatientDAO appointmentDAO = new AppointmentPatientDAO();

    // định dạng ngày
    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter displayDayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final Locale vietnameseLocale = new Locale("vi", "VN");

    private Doctor selectedDoctorForBooking = null;
    private DoctorSchedule selectedScheduleForBooking = null;

    @FXML
    public void initialize() {
        nameDoctor.textProperty().addListener((obs, oldText, newText) -> updateDoctorVBox());
        department.textProperty().addListener((obs, oldText, newText) -> updateDoctorVBox());

        updateDoctorVBox();
    }

    @FXML
    void saveTime(ActionEvent event) {
        // nhập triệu chứng
        String patientSymptom = symptom.getText();

        if (patientSymptom == null || patientSymptom.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập triệu chứng của bạn.");
            symptom.requestFocus();
            return;
        }

        if (selectedDoctorForBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lịch", "Vui lòng chọn một Bác Sĩ và Ngày Khám.");
            return;
        }

        if (selectedScheduleForBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lịch", "Bạn đã chọn ngày, vui lòng chọn một Giờ Khám cụ thể.");
            return;
        }

        // hàm xác nhận
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận đặt lịch");
        confirmAlert.setHeaderText("Bạn có chắc muốn đặt lịch hẹn này?");
        confirmAlert.setContentText(String.format(
                "Triệu chứng: %s\nBác sĩ: %s\nNgày: %s\nGiờ: %s",
                patientSymptom,
                selectedDoctorForBooking.getFullName(),
                selectedScheduleForBooking.getWorkDate(),
                selectedScheduleForBooking.getWorkTime()
        ));

        Optional<ButtonType> result = confirmAlert.showAndWait();

        // lưu dữ liệu lịch hẹn
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // lấy id của người dùng đăng nhập
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

                updateDoctorVBox();

                selectedDoctorForBooking = null;
                selectedScheduleForBooking = null;
                symptom.clear();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi CSDL", "Không thể lưu lịch hẹn. Vui lòng thử lại.");
            }
        }
    }

    // hàm lọc bác sĩ theo tên hoặc khoa
    private void updateDoctorVBox() {
        doctorVBox.getChildren().clear();

        String nameQuery = nameDoctor.getText();
        String deptQuery = department.getText();

        try {
            List<Doctor> filteredDoctors = doctorDAO.getAvailableDoctorsFiltered(nameQuery, deptQuery);

            for (Doctor doctor : filteredDoctors) {
                List<DoctorSchedule> schedules = scheduleDAO.getSchedulesByDoctor(doctor.getId());
                VBox doctorCard = createDoctorCard(doctor, schedules);
                doctorVBox.getChildren().add(doctorCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", "Không thể tải danh sách bác sĩ theo bộ lọc.");
        }
    }

    // tạo giao diện hiện ds bác sĩ
    private VBox createDoctorCard(Doctor doctor, List<DoctorSchedule> schedules) {
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

        VBox doctorInfoBox = new VBox(5);
        doctorInfoBox.setCursor(Cursor.HAND);

        Label nameLabel = new Label("Bác sĩ: " + doctor.getFullName());
        nameLabel.setFont(new Font("System Bold", 16));

        Label departmentLabel = new Label(doctor.getDepartmentName());
        departmentLabel.setFont(new Font("System Regular", 14));

        Label feeLabel = new Label(String.format("Phí khám: %,.0fđ", doctor.getConsultationFee()));
        feeLabel.setStyle("-fx-text-fill: #1e7fc9;");
        feeLabel.setFont(new Font("System Regular", 14));

        doctorInfoBox.getChildren().addAll(nameLabel, departmentLabel, feeLabel);

        // Container chứa lịch hẹn
        VBox scheduleContainer = new VBox(10);
        scheduleContainer.setStyle("-fx-padding: 10;");
        scheduleContainer.setVisible(false);
        scheduleContainer.setManaged(false);

        // Thanh chọn ngày
        HBox dateStrip = new HBox(10);
        ScrollPane dateScrollPane = new ScrollPane(dateStrip);
        dateScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        dateScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Dòng "Chọn giờ"
        Label timeLabel = new Label("Chọn giờ khám:");
        timeLabel.setFont(new Font("System Regular", 14));
        FlowPane timeSlotsPane = new FlowPane(10, 10);

        ToggleGroup timeToggleGroup = new ToggleGroup();

        // Khi click vào bác sĩ => hiện lịch hẹn
        doctorInfoBox.setOnMouseClicked(event -> {
            boolean showing = scheduleContainer.isVisible();
            if (showing) {
                scheduleContainer.setVisible(false);
                scheduleContainer.setManaged(false);
            } else {
                scheduleContainer.setVisible(true);
                scheduleContainer.setManaged(true);
                populateDateStrip(dateStrip, schedules, timeSlotsPane, timeToggleGroup);
            }
        });

        // Gắn các phần tử vào card
        scheduleContainer.getChildren().addAll(new Separator(), dateScrollPane, timeLabel, timeSlotsPane);
        card.getChildren().addAll(doctorInfoBox, scheduleContainer);

        return card;
    }

    /**
     * Tạo danh sách ngày hẹn khả dụng.
     * Khi click vào ngày => hiển thị danh sách giờ ngay bên dưới.
     */
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

    // chuyen trang
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
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Book1.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleBook2(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Book2.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
}