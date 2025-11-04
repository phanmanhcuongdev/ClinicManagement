package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.CreateAppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorScheduleDTO;
import com.oop4clinic.clinicmanagement.model.enums.DoctorScheduleStatus;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import com.oop4clinic.clinicmanagement.service.DoctorScheduleService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final DoctorService doctorService = new DoctorServiceImpl();
    private final DoctorScheduleService scheduleService = new DoctorScheduleServiceImpl();
    private final AppointmentService appointmentService = new AppointmentServiceImpl();

    private final DateTimeFormatter dbDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dbTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter displayDayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final Locale vietnameseLocale = new Locale("vi", "VN");

    private DoctorDTO selectedDoctorForBooking = null;
    private DoctorScheduleDTO selectedScheduleForBooking = null;
    private DepartmentDTO selectedDepartmentForBooking = null;

    @FXML public void initialize() {
        // Cần cập nhật danh sách khoa khi thay đổi tìm kiếm hoặc thay đổi ngày
        department.textProperty().addListener((obs, oldText, newText) -> updateDepartmentVBox());
        date.valueProperty().addListener((obs, oldDate, newDate) -> updateDepartmentVBox());
        updateDepartmentVBox();
    }

    // lưu lịch hẹn
    @FXML void saveTime(ActionEvent event) {
        String patientSymptom = symptom.getText();

        if (patientSymptom == null || patientSymptom.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập triệu chứng của bạn.");
            symptom.requestFocus();
            return;
        }

        if (selectedScheduleForBooking == null || selectedDoctorForBooking == null || selectedDepartmentForBooking == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn lịch", "Vui lòng chọn một Giờ Khám hợp lệ.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận đặt lịch");
        confirmAlert.setHeaderText("Bạn có chắc muốn đặt lịch hẹn này?");
        confirmAlert.setContentText(String.format(
                "Triệu chứng: %s\nKhoa: %s\nBác sĩ: %s\nNgày: %s\nGiờ: %s",
                patientSymptom,
                selectedDepartmentForBooking.getName(),
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
                newAppointment.setDepartmentId(selectedDepartmentForBooking.getId());
                newAppointment.setDoctorId(selectedDoctorForBooking.getId());

                // Chuyển đổi String (từ DTO Lịch) sang LocalDate/LocalTime (cho DTO Tạo hẹn)
                newAppointment.setAppointmentDate(LocalDate.parse(selectedScheduleForBooking.getWorkDate(), dbDateFormatter));
                newAppointment.setAppointmentTime(LocalTime.parse(selectedScheduleForBooking.getWorkTime(), dbTimeFormatter));

                newAppointment.setReason(patientSymptom);

                // 1. Gọi AppointmentService
                appointmentService.createAppointment(newAppointment);

                // 2. Gọi DoctorScheduleService
                scheduleService.updateScheduleStatus(selectedScheduleForBooking.getId(), DoctorScheduleStatus.OFF);

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đặt lịch thành công!");

                // Cập nhật lại giao diện và reset các biến
                updateDepartmentVBox();
                selectedDoctorForBooking = null;
                selectedScheduleForBooking = null;
                selectedDepartmentForBooking = null;
                symptom.clear();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Lỗi Nghiệp vụ", "Không thể lưu lịch hẹn: " + e.getMessage());
            }
        }
    }

    // hiển thị danh sách khoa
    private void updateDepartmentVBox() {
        departmentVBox.getChildren().clear();

        String deptNameQuery = department.getText();

        try {
            // 1. Lấy tất cả khoa có sẵn
            List<DepartmentDTO> allDepartments = departmentService.findAll();

            // 2. Lọc theo tên khoa (nếu có nhập)
            List<DepartmentDTO> filteredDepartments = allDepartments.stream()
                    .filter(d -> deptNameQuery.isEmpty() ||
                            d.getName().toLowerCase().contains(deptNameQuery.toLowerCase()))
                    .collect(Collectors.toList());

            // 3. Hiển thị tất cả các khoa đã lọc
            for (DepartmentDTO dept : filteredDepartments) {
                // Ta truyền List.of() vì logic lấy lịch trống đã chuyển vào createDepartmentCard
                VBox departmentCard = createDepartmentCard(dept);
                departmentVBox.getChildren().add(departmentCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", "Không thể tải danh sách khoa.");
        }
    }

    // giao diện card
    private VBox createDepartmentCard(DepartmentDTO dept) {
        VBox card = new VBox();
        card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #dcdcdc;
            -fx-border-width: 1;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 0; /* Giảm padding tổng thể */
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0.1, 0, 2);
            -fx-margin-bottom: 5;
        """);

        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-padding: 15;");
        headerBox.setCursor(Cursor.HAND);

        Label nameLabel = new Label(dept.getName());
        nameLabel.setFont(new Font("System Bold", 16));
        headerBox.getChildren().add(nameLabel);

        // Vùng chứa lịch khám (mặc định ẩn)
        VBox scheduleContainer = new VBox(10);
        scheduleContainer.setStyle("-fx-padding: 15; -fx-background-color: #f7f7f7;");
        scheduleContainer.setVisible(false);
        scheduleContainer.setManaged(false);

        headerBox.setOnMouseClicked(event -> {
            boolean isVisible = scheduleContainer.isVisible();
            scheduleContainer.setVisible(!isVisible);
            scheduleContainer.setManaged(!isVisible);

            // Nếu đang ẩn và người dùng click để mở rộng
            if (!isVisible) {
                // Tải lịch trống khi click
                loadAndDisplaySchedules(dept, scheduleContainer);
            }
        });


        card.getChildren().addAll(headerBox, scheduleContainer);
        return card;
    }

    // Phương thức mới: Tải và hiển thị lịch trống bên trong card
    private void loadAndDisplaySchedules(DepartmentDTO dept, VBox scheduleContainer) {
        scheduleContainer.getChildren().clear();
        LocalDate selectedDate = date.getValue();

        if (selectedDate == null) {
            // Trường hợp 1: Chưa chọn ngày
            Label errorLabel = new Label("Vui lòng chọn ngày khám ở ô 'Ngày hẹn' phía trên.");
            errorLabel.setStyle("-fx-text-fill: #a00000; -fx-font-style: italic;");
            scheduleContainer.getChildren().add(errorLabel);
            return;
        }

        try {
            final String dateStringQuery = selectedDate.format(dbDateFormatter);

            // 1. Gọi Service để lấy lịch trống khớp với Khoa VÀ Ngày đã chọn
            List<DoctorScheduleDTO> availableSchedules = scheduleService.getAvailableSchedulesByDeptAndDate(
                    dept.getId(),
                    dateStringQuery
            );

            if (availableSchedules.isEmpty()) {
                // Trường hợp 2: Đã chọn ngày nhưng không có lịch trống
                Label noScheduleLabel = new Label("Không tìm thấy lịch khám trống cho ngày " + selectedDate.format(displayDayMonthFormatter) + ".");
                noScheduleLabel.setStyle("-fx-text-fill: #a00000; -fx-font-style: italic;");
                scheduleContainer.getChildren().add(noScheduleLabel);
                return;
            }

            // Trường hợp 3: Có lịch trống -> Hiển thị Giờ khám

            FlowPane timeSlotsPane = new FlowPane(10, 10);
            ToggleGroup timeToggleGroup = new ToggleGroup();

            // Tải các nút giờ
            populateTimeSlots(timeSlotsPane, dept, availableSchedules, timeToggleGroup);

            scheduleContainer.getChildren().addAll(
                    new Label("Chọn giờ khám (" + selectedDate.format(displayDayMonthFormatter) + "):"),
                    timeSlotsPane
            );

        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Lỗi khi tải lịch: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #a00000;");
            scheduleContainer.getChildren().add(errorLabel);
        }
    }


    // tạo các nút giờ, khi chọn giờ thì gán selectedSchedule và selectedDoctor
    private void populateTimeSlots(FlowPane timeSlotsPane,
                                   DepartmentDTO dept,
                                   List<DoctorScheduleDTO> schedules,
                                   ToggleGroup timeToggleGroup) {
        timeSlotsPane.getChildren().clear();
        timeToggleGroup.getToggles().clear();

        // 1. Nhóm các lịch trình theo Giờ làm việc
        // Map<String (WorkTime), List<DoctorScheduleDTO>>
        Map<String, List<DoctorScheduleDTO>> groupedSchedules = schedules.stream()
                .collect(Collectors.groupingBy(DoctorScheduleDTO::getWorkTime));

        // 2. Sắp xếp các giờ
        List<String> sortedTimes = groupedSchedules.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        // 3. Tạo nút cho mỗi Giờ làm việc duy nhất
        for (String workTime : sortedTimes) {
            List<DoctorScheduleDTO> slotsAtThisTime = groupedSchedules.get(workTime);

            ToggleButton timeButton = new ToggleButton(workTime);
            timeButton.setToggleGroup(timeToggleGroup);
            timeButton.getStyleClass().add("time-slot-button");

            timeButton.setOnAction(ev -> {
                DoctorScheduleDTO chosenSlot = slotsAtThisTime.get(0);

                try {
                    // Dùng findById của DoctorService (đã có)
                    DoctorDTO doctor = doctorService.findById(chosenSlot.getDoctorId());

                    selectedScheduleForBooking = chosenSlot;
                    selectedDoctorForBooking = doctor;
                    selectedDepartmentForBooking = dept;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lấy thông tin bác sĩ.");
                }
            });

            timeSlotsPane.getChildren().add(timeButton);
        }
    }

        // tbao
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
    @FXML void handleBook1(ActionEvent event) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}