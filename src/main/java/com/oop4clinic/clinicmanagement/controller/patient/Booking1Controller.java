package com.oop4clinic.clinicmanagement.controller.patient;

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

/**
 * Controller này quản lý màn hình "Booking1" (Đặt lịch - Giai đoạn 1).
 * Chức năng:
 * 1. Tìm kiếm bác sĩ.
 * 2. Hiển thị lịch làm việc có sẵn của bác sĩ (7 ngày tới).
 * 3. Cho phép bệnh nhân chọn ngày, giờ và nhập triệu chứng.
 * 4. Lưu lịch hẹn mới.
 */
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
    @FXML private Button saveButton;
    @FXML private VBox doctorVBox;

    private final DoctorService doctorService = new DoctorServiceImpl();
    private final DoctorScheduleService scheduleService = new DoctorScheduleServiceImpl();
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dbTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private final DateTimeFormatter displayDayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");

    private DoctorDTO selectedDoctorForBooking = null;
    // bac si duoc chon
    private DoctorScheduleDTO selectedScheduleForBooking = null;
    // lich duoc chon

    @FXML public void initialize() {
        // lich lam viec trong 7 ngay toi của bac si
        scheduleService.ensureSchedulesExistForNextDays(7);

        // o tim kiem ten bac si
        nameDoctor.textProperty().addListener((obs, oldText, newText) -> {
            updateDoctorVBox();
        });

        // o tim kiem khoa bac si
        department.textProperty().addListener((obs, oldText, newText) -> {
            updateDoctorVBox();
        });
        updateDoctorVBox();
    }

    // xu ly du lieu khi an nut dat lich
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

        // hop thoai xac nhan
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

                // tao lich hen va luu vao du lieu
                CreateAppointmentDTO newAppointment = new CreateAppointmentDTO();
                newAppointment.setPatientId(currentPatientId);
                newAppointment.setDepartmentId(selectedDoctorForBooking.getDepartmentId());
                newAppointment.setDoctorId(selectedDoctorForBooking.getId());
                newAppointment.setAppointmentDate(LocalDate.parse(selectedScheduleForBooking.getWorkDate(), dbDateFormatter));
                newAppointment.setAppointmentTime(LocalTime.parse(selectedScheduleForBooking.getWorkTime(), dbTimeFormatter));
                newAppointment.setReason(patientSymptom);

                // 7. Gọi service để thực sự tạo lịch hẹn trong CSDL
                appointmentService.createAppointment(newAppointment);

                // cap nhat lich duoc chon là off
                scheduleService.updateScheduleStatus(selectedScheduleForBooking.getId(), DoctorScheduleStatus.OFF);

                // reset du lieu sau khi dat lich thanh cong
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
            // tim bac si theo ten va loc lich trong
            List<DoctorDTO> serviceResults = doctorService.searchDoctors(nameQuery, null, DoctorStatus.ACTIVE);

            List<DoctorDTO> filteredDoctors;
            if (deptQuery.isEmpty()) {
                filteredDoctors = serviceResults;

            } else {
                List<DoctorDTO> tempList = new ArrayList<>();

                for (DoctorDTO doctor : serviceResults) {
                    String departmentName = doctor.getDepartmentName();
                    if (departmentName != null) {
                        if (departmentName.toLowerCase().contains(deptQuery)) {
                            tempList.add(doctor);
                        }
                    }
                }
                filteredDoctors = tempList;
            }

            // hien thi ds bac si co lich trong
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

    // giao dien card
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

        // xu ly su kien khi an chon bac si
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

    // ve nut cho gio hen
    private void populateDateStrip(HBox dateStrip, DoctorDTO doctor, List<DoctorScheduleDTO> schedules, FlowPane timeSlotsPane, ToggleGroup timeToggleGroup) {
        dateStrip.getChildren().clear();

        Set<String> availableDates = schedules.stream()
                .map(DoctorScheduleDTO::getWorkDate)
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        ToggleGroup dateToggleGroup = new ToggleGroup();

        for (int i = 0; i < 7; i++) {
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
                        // Reset trạng thái
                        this.selectedScheduleForBooking = null;
                        this.selectedDoctorForBooking = null;

                        timeSlotsPane.getChildren().clear();
                        timeToggleGroup.getToggles().clear();

                        // lọc và sắp xếp các giờ khám chi cho ngày vừa chọn
                        List<DoctorScheduleDTO> slotsForDay = schedules.stream()
                                .filter(s -> Objects.equals(s.getWorkDate(), dateStringDB)) // Lọc theo ngày
                                .sorted(Comparator.comparing(DoctorScheduleDTO::getWorkTime))
                                .toList();

                        // tao nut cho tung gio kham
                        for (DoctorScheduleDTO schedule : slotsForDay) {
                            ToggleButton timeButton = new ToggleButton(schedule.getWorkTime());
                            timeButton.setUserData(schedule);
                            timeButton.setToggleGroup(timeToggleGroup);
                            timeButton.getStyleClass().add("time-slot-button");

                            // xu ly su kien khi chon gio kham
                            timeButton.setOnAction(ev -> {
                                // luu du lieu khi chon xong
                                selectedScheduleForBooking = schedule;
                                selectedDoctorForBooking = doctor;
                            });

                            timeSlotsPane.getChildren().add(timeButton);
                        }
                    }
                    else {
                        // restart dữ liệu
                        timeSlotsPane.getChildren().clear();
                        timeToggleGroup.getToggles().clear();
                        this.selectedScheduleForBooking = null;
                        this.selectedDoctorForBooking = null;
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

    private void switchScene(String fxmlFile, Control currentControl) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Scene scene = currentControl.getScene();
        scene.setRoot(root);
    }

    @FXML void handleInfo(ActionEvent event) throws IOException {
        switchScene("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml", homeButton);
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        switchScene("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml", homeButton);
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        switchScene("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml", homeButton);
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        switchScene("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml", homeButton);
    }

    @FXML void handleLogout(ActionEvent event) throws IOException {
        UserSession.clear();
        switchScene("/com/oop4clinic/clinicmanagement/fxml/Login.fxml", homeButton);
    }
}