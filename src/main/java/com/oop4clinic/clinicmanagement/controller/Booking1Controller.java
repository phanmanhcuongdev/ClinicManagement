package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.CreateAppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorScheduleDTO;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import com.oop4clinic.clinicmanagement.service.DoctorScheduleService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.DoctorServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.DoctorScheduleServiceImpl;
import com.oop4clinic.clinicmanagement.util.UserSession;
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
import java.time.LocalDate;
import java.time.LocalTime;
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

    private final DoctorService doctorService = new DoctorServiceImpl();
    private final DoctorScheduleService scheduleService = new DoctorScheduleServiceImpl();
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dbTimeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // <- Cần cho DTO
    private final DateTimeFormatter displayDayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final Locale vietnameseLocale = new Locale("vi", "VN");

    private DoctorDTO selectedDoctorForBooking = null;
    private DoctorScheduleDTO selectedScheduleForBooking = null;

    @FXML public void initialize() {
        nameDoctor.textProperty().addListener((obs, oldText, newText) -> updateDoctorVBox());
        department.textProperty().addListener((obs, oldText, newText) -> updateDoctorVBox());
        updateDoctorVBox();
    }

    @FXML void saveTime(ActionEvent event) {
        String patientSymptom = symptom.getText();

        if (patientSymptom == null || patientSymptom.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập triệu chứng của bạn.");
            symptom.requestFocus();
            return;
        }

        if (selectedDoctorForBooking == null || selectedScheduleForBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lịch", "Vui lòng chọn một Bác Sĩ và một Giờ Khám cụ thể.");
            return;
        }

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

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Integer currentPatientId = UserSession.getCurrentId();

                if (currentPatientId == null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi đăng nhập", "Không tìm thấy thông tin bệnh nhân đăng nhập!");
                    return;
                }

                CreateAppointmentDTO newAppointment = new CreateAppointmentDTO();
                newAppointment.setPatientId(currentPatientId);
                newAppointment.setDepartmentId(selectedDoctorForBooking.getDepartmentId());
                newAppointment.setDoctorId(selectedDoctorForBooking.getId());

                newAppointment.setAppointmentDate(LocalDate.parse(selectedScheduleForBooking.getWorkDate(), dbDateFormatter));
                newAppointment.setAppointmentTime(LocalTime.parse(selectedScheduleForBooking.getWorkTime(), dbTimeFormatter));

                newAppointment.setReason(patientSymptom);

                appointmentService.createAppointment(newAppointment);

                scheduleService.updateScheduleStatus(selectedScheduleForBooking.getId(), DoctorScheduleStatus.OFF); // Dùng Enum

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đặt lịch thành công!");

                updateDoctorVBox();
                selectedDoctorForBooking = null;
                selectedScheduleForBooking = null;
                symptom.clear();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi Nghiệp vụ", "Không thể lưu lịch hẹn: " + e.getMessage());
            }
        }
    }

    private void updateDoctorVBox() {
        doctorVBox.getChildren().clear();
        String nameQuery = nameDoctor.getText();
        String deptQuery = department.getText().trim().toLowerCase();

        try {
            List<DoctorDTO> serviceResults = doctorService.searchDoctors(nameQuery, null, DoctorStatus.ACTIVE);
            List<DoctorDTO> filteredDoctors;
            if (deptQuery.isEmpty()) {
                filteredDoctors = serviceResults;
            } else {
                filteredDoctors = serviceResults.stream()
                        .filter(d -> d.getDepartmentName() != null &&
                                d.getDepartmentName().toLowerCase().contains(deptQuery))
                        .collect(Collectors.toList());
            }

            // lay lich cua bac si duoc tim kiem
            for (DoctorDTO doctor : filteredDoctors) {
                List<DoctorScheduleDTO> schedules = scheduleService.getAvailableSchedulesByDoctorId(doctor.getId());
                if (!schedules.isEmpty()) {
                    VBox doctorCard = createDoctorCard(doctor, schedules);
                    doctorVBox.getChildren().add(doctorCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", "Không thể tải danh sách bác sĩ.");
        }
    }

    // giao diện hiển thị bác si
    private VBox createDoctorCard(DoctorDTO doctor, List<DoctorScheduleDTO> schedules) {
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

        VBox scheduleContainer = new VBox(10);
        scheduleContainer.setStyle("-fx-padding: 10;");
        scheduleContainer.setVisible(false);
        scheduleContainer.setManaged(false);

        HBox dateStrip = new HBox(10);
        ScrollPane dateScrollPane = new ScrollPane(dateStrip);
        dateScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        dateScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Label timeLabel = new Label("Chọn giờ khám:");
        timeLabel.setFont(new Font("System Regular", 14));
        FlowPane timeSlotsPane = new FlowPane(10, 10);
        ToggleGroup timeToggleGroup = new ToggleGroup();

        doctorInfoBox.setOnMouseClicked(event -> {
            boolean showing = scheduleContainer.isVisible();
            scheduleContainer.setVisible(!showing);
            scheduleContainer.setManaged(!showing);

            if (scheduleContainer.isVisible()) {
                populateDateStrip(dateStrip, doctor, schedules, timeSlotsPane, timeToggleGroup);
            }
        });

        scheduleContainer.getChildren().addAll(new Separator(), dateScrollPane, timeLabel, timeSlotsPane);
        card.getChildren().addAll(doctorInfoBox, scheduleContainer);
        return card;
    }


    private void populateDateStrip(HBox dateStrip, DoctorDTO doctor, List<DoctorScheduleDTO> schedules, FlowPane timeSlotsPane, ToggleGroup timeToggleGroup) {
        dateStrip.getChildren().clear();

        Set<String> availableDates = schedules.stream()
                .map(DoctorScheduleDTO::getWorkDate)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        ToggleGroup dateToggleGroup = new ToggleGroup(); // <- Cần group cho Date

        for (int i = 0; i < 10; i++) {
            LocalDate date = today.plusDays(i);
            String dateStringDB = date.format(dbDateFormatter);
            String dayOfWeek = "Thứ " + (date.getDayOfWeek().getValue() + 1);
            if (date.getDayOfWeek().getValue() == 7) dayOfWeek = "Chủ nhật";
            String dayMonth = date.format(displayDayMonthFormatter);

            ToggleButton dateButton = new ToggleButton(dayOfWeek + "\n" + dayMonth);
            dateButton.getStyleClass().add("date-button");
            dateButton.setToggleGroup(dateToggleGroup);
            dateStrip.getChildren().add(dateButton);

            if (!availableDates.contains(dateStringDB)) {
                dateButton.setDisable(true);
            } else {
                dateButton.setOnAction(e -> {
                    if (dateButton.isSelected()) {
                        this.selectedScheduleForBooking = null;
                        this.selectedDoctorForBooking = null;

                        timeSlotsPane.getChildren().clear();
                        timeToggleGroup.getToggles().clear();

                        List<DoctorScheduleDTO> slotsForDay = schedules.stream()
                                .filter(s -> Objects.equals(s.getWorkDate(), dateStringDB))
                                .sorted(Comparator.comparing(DoctorScheduleDTO::getWorkTime))
                                .toList();

                        for (DoctorScheduleDTO schedule : slotsForDay) {
                            ToggleButton timeButton = new ToggleButton(schedule.getWorkTime());
                            timeButton.setUserData(schedule);
                            timeButton.setToggleGroup(timeToggleGroup);
                            timeButton.getStyleClass().add("time-slot-button");

                            timeButton.setOnAction(ev -> {
                                selectedScheduleForBooking = schedule;
                                selectedDoctorForBooking = doctor;
                            });

                            timeSlotsPane.getChildren().add(timeButton);
                        }
                    }
                    else {
                        timeSlotsPane.getChildren().clear();
                        timeToggleGroup.getToggles().clear();
                        this.selectedScheduleForBooking = null;
                        this.selectedDoctorForBooking = null;
                    }
                });
            }
        }
    }

    //tbao
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

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
    @FXML void handleBook2(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking2.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}