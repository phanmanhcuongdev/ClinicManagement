package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.DashboardSummaryDTO;
import com.oop4clinic.clinicmanagement.model.entity.Appointment;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.service.AdminDashboardService;
import com.oop4clinic.clinicmanagement.service.impl.AdminDashboardServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller chính cho màn Admin (MenuAdmin.fxml).
 * - Sidebar gọi openPatients(), openDoctors(), ...
 * - Center mặc định là dashboard
 * - Dashboard có thể reload runtime qua refreshDashboard()
 */
public class MenuAdminController {

    // ================== SERVICE ==================
    private final AdminDashboardService adminDashboardService = new AdminDashboardServiceImpl();

    // ================== ROOT LAYOUT ==================
    @FXML
    private BorderPane root;

    // Node dashboard gốc ban đầu (center trong FXML gốc).
    private Node dashboardContent;

    // ================== KPI LABELS ==================
    @FXML private Label lblTotalPatients;
    @FXML private Label lblPatientsDelta;          // ví dụ: "+2 hôm nay" (hiện chưa tính, sẽ để "--")
    @FXML private Label lblTotalDoctors;
    @FXML private Label lblDoctorsActive;
    @FXML private Label lblAppointmentsToday;
    @FXML private Label lblNextAppointmentTime;    // sẽ set lịch hẹn sớm nhất trong upcoming
    @FXML private Label lblRevenueToday;
    @FXML private Label lblRevenueTrend;           // ví dụ: "+5% vs hôm qua" (chưa có logic -> để "--")
    @FXML private Label lblRevenueWeek;
    @FXML private Label lblRevenueMonth;

    // ================== CHART (lượt khám) ==================
    @FXML private BarChart<String, Number> chartVisits;
    @FXML private CategoryAxis chartVisitsXAxis; // phải khai báo trong FXML như fx:id="chartVisitsXAxis"
    @FXML private NumberAxis    chartVisitsYAxis; // fx:id="chartVisitsYAxis"

    // ================== BỆNH NHÂN MỚI ==================
    @FXML private TableView<PatientRow> tblRecentPatients;
    @FXML private TableColumn<PatientRow, Integer>   colPatientId;
    @FXML private TableColumn<PatientRow, String>    colPatientName;
    @FXML private TableColumn<PatientRow, LocalDate> colPatientDob;
    @FXML private TableColumn<PatientRow, String>    colPatientPhone;

    // ================== LỊCH HẸN SẮP TỚI ==================
    @FXML private VBox upcomingAppointmentsBox;

    // ========================================================
    //  INIT
    // ========================================================
    @FXML
    private void initialize() {
        // Giữ lại dashboard center ban đầu để có thể quay lại sau khi mở màn khác
        dashboardContent = root.getCenter();

        // Chuẩn bị cấu hình bảng "Bệnh nhân mới"
        initRecentPatientsTableColumns();

        // Lần đầu mở MenuAdmin.fxml chính là dashboard,
        // nên gọi refreshDashboard() luôn để đổ data.
        refreshDashboard();
    }

    // ========================================================
    //  SIDEBAR ACTIONS
    // ========================================================

    @FXML
    private void openDashboard() {
        // Đưa lại dashboardContent vào center (phòng trường hợp đang ở màn khác)
        if (dashboardContent != null) {
            root.setCenter(dashboardContent);
        }
        // Cập nhật số liệu dashboard
        refreshDashboard();
    }

    @FXML
    private void openPatients() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/PatientManagement.fxml");
    }

    @FXML
    private void openDoctors() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DoctorManagement.fxml");
    }

    @FXML
    private void openDepartments() {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/DepartmentManagement.fxml");
    }

    @FXML
    private void openAppointments() {
        // Chuẩn bị sau này khi có AppointmentManagement.fxml
        System.out.println("TODO: openAppointments()");
        // loadCenterView("/com/oop4clinic/clinicmanagement/fxml/AppointmentManagement.fxml");
    }

    @FXML
    private void openRecords() {
        System.out.println("TODO: openRecords()");
        // loadCenterView("/com/oop4clinic/clinicmanagement/fxml/MedicalRecordManagement.fxml");
    }

    @FXML
    private void openInvoices() {
        System.out.println("TODO: openInvoices()");
        // loadCenterView("/com/oop4clinic/clinicmanagement/fxml/InvoiceManagement.fxml");
    }

    @FXML
    private void openSettings()
    {
        loadCenterView("/com/oop4clinic/clinicmanagement/fxml/AdminSettings.fxml");
    }

    // ========================================================
    //  HELPER: LOAD VIEW VÀO CENTER
    // ========================================================
    private void loadCenterView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            root.setCenter(view);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // ========================================================
    //  DASHBOARD RUNTIME UPDATE
    // ========================================================
    public void refreshDashboard() {
        DashboardSummaryDTO dto;

        try {
            dto = adminDashboardService.loadDashboardData();
        } catch (Exception e) {
            // nếu service chưa nối EntityManager hoặc DB fail,
            // tránh app chết UI: chỉ log và đặt placeholder.
            e.printStackTrace();
            applyDashboardFallback();
            return;
        }

        // ---------- KPI section ----------
        lblTotalPatients.setText(String.valueOf(dto.getTotalPatients()));
        lblTotalDoctors.setText(String.valueOf(dto.getTotalDoctors()));
        lblDoctorsActive.setText(dto.getActiveDoctors() + " đang làm việc");

        lblAppointmentsToday.setText(String.valueOf(dto.getAppointmentsToday()));

        lblRevenueToday.setText(formatCurrency(dto.getRevenueToday()));
        lblRevenueWeek.setText(formatCurrency(dto.getRevenueThisWeek()));
        lblRevenueMonth.setText(formatCurrency(dto.getRevenueThisMonth()));

        // Các nhãn chưa có logic tính: để tạm "--"
        lblPatientsDelta.setText("--");
        lblRevenueTrend.setText("--");

        // ---------- Upcoming appointments ----------
        renderUpcomingAppointments(dto);

        // ---------- Bệnh nhân mới ----------
        renderRecentPatients(dto);

        // ---------- Biểu đồ lượt khám ----------
        renderChart(dto);
    }

    // ========================================================
    //  SUPPORT: KPI fallback khi lỗi service
    // ========================================================
    private void applyDashboardFallback() {
        lblTotalPatients.setText("—");
        lblPatientsDelta.setText("—");
        lblTotalDoctors.setText("—");
        lblDoctorsActive.setText("—");
        lblAppointmentsToday.setText("—");
        lblNextAppointmentTime.setText("—");
        lblRevenueToday.setText("—");
        lblRevenueTrend.setText("—");
        lblRevenueWeek.setText("—");
        lblRevenueMonth.setText("—");

        // clear UI components
        chartVisits.getData().clear();
        tblRecentPatients.getItems().clear();
        upcomingAppointmentsBox.getChildren().clear();
    }

    // ========================================================
    //  TABLE "Bệnh nhân mới"
    // ========================================================
    /**
     * Ở FXML bạn khai báo TableView generic là gì?
     * Mình dùng lớp view-model nhỏ PatientRow để không lộ entity vào UI trực tiếp.
     */
    public static class PatientRow {
        private final Integer id;
        private final String  fullName;
        private final LocalDate dob;
        private final String  phone;

        public PatientRow(Integer id, String fullName, LocalDate dob, String phone) {
            this.id = id;
            this.fullName = fullName;
            this.dob = dob;
            this.phone = phone;
        }

        public Integer getId() { return id; }
        public String getFullName() { return fullName; }
        public LocalDate getDob() { return dob; }
        public String getPhone() { return phone; }
    }

    private void initRecentPatientsTableColumns() {
        // đảm bảo generic type đúng với PatientRow
        // (FXML cần <TableView fx:id="tblRecentPatients"> mà không có type generic,
        // thì ở runtime ta cast được.)
        @SuppressWarnings("unchecked")
        TableView<PatientRow> casted = (TableView<PatientRow>) (TableView<?>) tblRecentPatients;
        tblRecentPatients = casted;

        colPatientId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPatientDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colPatientPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void renderRecentPatients(DashboardSummaryDTO dto) {
        tblRecentPatients.getItems().clear();

        if (dto.getRecentPatients() == null) return;

        for (Patient p : dto.getRecentPatients()) {
            PatientRow row = new PatientRow(
                    p.getId(),
                    p.getFullName(),
                    p.getDateOfBirth(),
                    p.getPhone()
            );
            tblRecentPatients.getItems().add(row);
        }
    }

    // ========================================================
    //  UPCOMING APPOINTMENTS (VBox danh sách HBox)
    // ========================================================
    private void renderUpcomingAppointments(DashboardSummaryDTO dto) {
        upcomingAppointmentsBox.getChildren().clear();
        lblNextAppointmentTime.setText("--");

        if (dto.getUpcomingAppointments() == null || dto.getUpcomingAppointments().isEmpty()) {
            // Không có lịch -> hiển thị 1 label trống
            Label empty = new Label("Không có lịch hẹn sắp tới.");
            empty.setStyle("-fx-text-fill:#1e80c7;");
            upcomingAppointmentsBox.getChildren().add(empty);
            return;
        }

        // Lấy lịch sớm nhất để show lên lblNextAppointmentTime
        Appointment first = dto.getUpcomingAppointments().get(0);
        if (first.getStartTime() != null) {
            lblNextAppointmentTime.setText(
                    first.getStartTime().toLocalTime().toString()
            );
        }

        // Render từng lịch dưới dạng HBox "HH:mm  |  Tên bệnh nhân  |  Bác sĩ/Khoa"
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm dd/MM");
        for (Appointment ap : dto.getUpcomingAppointments()) {
            String timeStr = ap.getStartTime() == null
                    ? "--:--"
                    : ap.getStartTime().format(timeFmt);

            String patientName = (ap.getPatient() != null)
                    ? ap.getPatient().getFullName()
                    : "(không rõ bệnh nhân)";

            String doctorName;
            if (ap.getDoctor() != null) {
                doctorName = ap.getDoctor().getFullName();
            } else if (ap.getDepartment() != null) {
                doctorName = ap.getDepartment().getName();
            } else {
                doctorName = "(chưa phân công)";
            }

            Label lblTime   = new Label(timeStr);
            Label lblPName  = new Label(patientName);
            Label lblDoctor = new Label(doctorName);

            lblTime.setStyle("-fx-font-weight:bold; -fx-text-fill:#1e80c7;");
            lblPName.setStyle("-fx-text-fill:#000000;");
            lblDoctor.setStyle("-fx-text-fill:#555555;");

            HBox row = new HBox(10, lblTime, lblPName, lblDoctor);
            row.setStyle("-fx-background-color:#ffffffaa; -fx-padding:6 10 6 10; -fx-background-radius:8;");
            upcomingAppointmentsBox.getChildren().add(row);
        }
    }

    // ========================================================
    //  CHART (BarChart số lượt khám 7 ngày gần nhất)
    // ========================================================
    private void renderChart(DashboardSummaryDTO dto) {
        chartVisits.getData().clear();
        if (dto.getVisitCountsLast7Days() == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // dto.getVisitCountsLast7Days() là LinkedHashMap<String, Long> giữ đúng thứ tự ngày
        dto.getVisitCountsLast7Days().forEach((dayStr, countVal) -> {
            series.getData().add(new XYChart.Data<>(dayStr, countVal));
        });

        chartVisits.getData().add(series);

        // nhãn trục Y: số lượt
        if (chartVisitsYAxis != null) {
            chartVisitsYAxis.setLabel("Số lượt khám");
        }
        if (chartVisitsXAxis != null) {
            chartVisitsXAxis.setLabel("Ngày");
        }
    }

    // ========================================================
    //  FORMAT TIỀN
    // ========================================================
    private String formatCurrency(double v) {
        // ví dụ: 350000 -> "350.000 đ"
        return String.format("%,.0f đ", v).replace(',', '.');
    }
}
