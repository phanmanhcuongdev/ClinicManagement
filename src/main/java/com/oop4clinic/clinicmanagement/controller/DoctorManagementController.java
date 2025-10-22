package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.ValidationUtils;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.DoctorServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import static com.oop4clinic.clinicmanagement.ValidationUtils.*;

public class DoctorManagementController {
    private final DepartmentService departmentService = new DepartmentServiceImpl();

    private Integer selectedDoctorId;

    private final DoctorService doctorService = new DoctorServiceImpl();
    @FXML
    private TextField txtFullName;

    @FXML
    private ComboBox<Gender> cbGender;

    @FXML
    private DatePicker dpDob;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextField txtEmail;

    @FXML
    private ComboBox<DepartmentDTO> cbDepartmentForm;

    @FXML
    private TextField txtFee;

    @FXML
    private  TextArea txtAddress;

    @FXML
    private TextArea txtNotes;

    @FXML
    private ComboBox<DoctorStatus> doctorStatus;

    @FXML
    private SplitPane split;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnSave;

    @FXML private TableView<DoctorDTO> tblDoctors;
    @FXML private TableColumn<DoctorDTO, String> colCode;
    @FXML private TableColumn<DoctorDTO, String> colFullName;
    @FXML private TableColumn<DoctorDTO, String> colDepartment;
    @FXML private TableColumn<DoctorDTO, String> colGender;
    @FXML private TableColumn<DoctorDTO, String> colStatus;
    @FXML private TableColumn<DoctorDTO, String> colPhone;
    @FXML private TableColumn<DoctorDTO, String> colEmail;
    @FXML private TableColumn<DoctorDTO, String> colFee;

    @FXML
    public void initialize()
    {
        initDepartmentCombo();
        initGender();
        initDoctorStatus();
        holdPosition();
        initDoctorTable();
        refreshTable();

    }

    @FXML
    private void onDoctorTableClicked(javafx.scene.input.MouseEvent event)
    {
        DoctorDTO selectedDoctor = tblDoctors.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) return;
        DoctorDTO detail = doctorService.findById(selectedDoctor.getId());

        selectedDoctorId = selectedDoctor.getId();
        txtFullName.setText(detail.getFullName());
        cbGender.setValue(detail.getGender());
        dpDob.setValue(detail.getDateOfBirth());
        txtPhone.setText(detail.getPhone());
        txtEmail.setText(detail.getEmail());
        txtFee.setText(detail.getConsultationFee() == null ? "" :String.valueOf(detail.getConsultationFee()));
        txtAddress.setText(detail.getAddress());
        doctorStatus.setValue(detail.getDoctorStatus());
        txtNotes.setText(detail.getNotes());

        for (DepartmentDTO dep : cbDepartmentForm.getItems()) {
            if (dep.getId().equals(detail.getDepartmentId())) {
                cbDepartmentForm.setValue(dep);
                break;
            }
        }

        btnEdit.setDisable(false);
        btnSave.setDisable(true);

    }

    @FXML
    private void onUpdateDoctor()
    {

        String fullName = trimOrNull(txtFullName.getText());
        Gender gender = cbGender.getValue();
        var dob = dpDob.getValue();
        String phone = trimOrNull(txtPhone.getText());
        String email = trimOrNull(txtEmail.getText());
        DepartmentDTO dep = cbDepartmentForm.getValue();
        String address = trimOrNull(txtAddress.getText());
        Double fee = null;
        DoctorStatus status = doctorStatus.getValue();
        String notes = trimOrNull(txtNotes.getText());

        if (isBlank(fullName)) { warn("Vui lòng nhập Họ tên."); return; }
        if (gender == null)    { warn("Vui lòng chọn Giới tính."); return; }
        if (status == null)    { warn("Vui lòng chọn Trạng thái."); return; }
        if (!isValidDob(dob))  { warn("Ngày sinh không hợp lệ."); return; }
        if (isBlank(phone))    { warn("Vui lòng nhập SĐT."); return; }
        if (!isValidPhone(phone)) { warn("SĐT không hợp lệ (10–11 chữ số)."); return; }
        if (email != null && !isValidEmail(email)) { warn("Email không hợp lệ."); return; }
        if (dep == null)       { warn("Vui lòng chọn Khoa."); return; }
        try {
            fee = ValidationUtils.parseFee(txtFee.getText());
        } catch (NumberFormatException ex) {
            warn("Phí khám không hợp lệ (phải là số ≥ 0).");
            return;
        }

        DoctorDTO dto = new DoctorDTO();
        dto.setId(selectedDoctorId);
        dto.setFullName(fullName);
        dto.setGender(gender);
        dto.setDateOfBirth(dob);
        dto.setPhone(phone);
        dto.setEmail(email);
        dto.setAddress(address);
        dto.setConsultationFee(fee);
        dto.setDepartmentId(dep.getId());
        dto.setDoctorStatus(status);
        dto.setNotes(notes);

        try {
            DoctorDTO saved = doctorService.update(dto); // service lo transaction/unique check
            info("Đã sửa bác sĩ: " + saved.getFullName() + " (ID=" + saved.getId() + ")");
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException dup) { // ví dụ SĐT/Email trùng
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }
    }

    private void initDoctorTable()
    {
        colCode.setCellValueFactory(c->{
            var dto = c.getValue();
            String prefix = codePrefixFromDepartmentName(dto.getDepartmentName());
            String code = (dto.getId() == null) ? "" : prefix + String.format("%03d",dto.getId());
            return new javafx.beans.property.ReadOnlyStringWrapper(code);
        });

        colFullName.setCellValueFactory(c -> wrap(c.getValue().getFullName()));
        colDepartment.setCellValueFactory(c -> wrap(c.getValue().getDepartmentName()));

        colGender.setCellValueFactory(c -> {
            var g = c.getValue().getGender();
            return wrap(g == null ? "" : genderVi(g));
        });

        colStatus.setCellValueFactory(c -> {
            var st = c.getValue().getDoctorStatus();
            return wrap(st == null ? "" : statusVi(st));
        });
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String text,boolean empty)
            {
                super.updateItem(text,empty);
                if (empty || text == null || text.isBlank())
                {
                    setText(null);setStyle("");
                    return;
                }
                setText(text);
                // màu nhẹ theo trạng thái
                switch (text) {
                    case "Hoạt động"      -> setStyle("-fx-text-fill: #1a7f37; -fx-font-weight: bold;"); // xanh
                    case "Ngưng hoạt động"-> setStyle("-fx-text-fill: #b42318; -fx-font-weight: bold;"); // đỏ
                    case "Chờ duyệt"      -> setStyle("-fx-text-fill: #b78103; -fx-font-weight: bold;"); // vàng
                    case "Đình chỉ"       -> setStyle("-fx-text-fill: #8a2be2; -fx-font-weight: bold;"); // tím
                    case "Tạm nghỉ"       -> setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");    // xám
                    default               -> setStyle("");
                }
            }
        });
        colPhone.setCellValueFactory(c -> wrap(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> wrap(c.getValue().getEmail()));

        // Phí khám: dùng consultationFee; nếu null tạm để "—"
        // (nếu muốn fallback baseFee của khoa, thêm field departmentBaseFee vào DTO rồi sửa ở đây)
        colFee.setCellValueFactory(c -> {
            Double fee = c.getValue().getConsultationFee();
            return wrap(fee == null ? "—" : formatVnd(fee));
        });

        // Bảng tự giãn đầy chiều rộng
        tblDoctors.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private static String formatVnd(double v) {
        String s = String.format("%,.0f đ", v).replace(',', '.'); // 350000 -> 350.000 đ
        return s;
    }

    private static String codePrefixFromDepartmentName(String name) {
        if (name == null) return "KH";
        return switch (name.trim()) {
            case "Khoa Tim Mạch" -> "TM";
            case "Khoa Da Liễu" -> "DL";
            case "Khoa Thần Kinh" -> "TK";
            case "Khoa Nội Tổng Quát" -> "NTQ";
            case "Khoa Nhi" -> "NHI";
            case "Khoa Sản" -> "SAN";
            case "Khoa Phụ Khoa" -> "PK";
            case "Khoa Mắt" -> "MAT";
            case "Khoa Nha Khoa" -> "RHM";
            case "Khoa Tai Mũi Họng" -> "TMH";
            case "Khoa Tâm Thần" -> "TT";
            default -> "KH";
        };
    }

    private static javafx.beans.property.ReadOnlyStringWrapper wrap(String s) {
        return new javafx.beans.property.ReadOnlyStringWrapper(s == null ? "" : s);
    }

    private void holdPosition()
    {
        javafx.application.Platform.runLater(() -> {
            split.setDividerPositions(0.75);
            // Mỗi lần SplitPane đổi kích thước → set lại tỉ lệ
            split.widthProperty().addListener((obs, ov, nv) -> split.setDividerPositions(0.75));
            split.heightProperty().addListener((obs, ov, nv) -> split.setDividerPositions(0.75));
        });
    }

    @FXML
    private void onSaveDoctor()
    {
        String fullName = trimOrNull(txtFullName.getText());
        Gender gender = cbGender.getValue();
        var dob = dpDob.getValue();
        String phone = trimOrNull(txtPhone.getText());
        String email = trimOrNull(txtEmail.getText());
        DepartmentDTO dep = cbDepartmentForm.getValue();
        String address = trimOrNull(txtAddress.getText());
        Double fee = null;
        DoctorStatus status = doctorStatus.getValue();
        String notes = trimOrNull(txtNotes.getText());

        if (isBlank(fullName)) { warn("Vui lòng nhập Họ tên."); return; }
        if (gender == null)    { warn("Vui lòng chọn Giới tính."); return; }
        if (status == null)    { warn("Vui lòng chọn Trạng thái."); return; }
        if (!isValidDob(dob))  { warn("Ngày sinh không hợp lệ."); return; }
        if (isBlank(phone))    { warn("Vui lòng nhập SĐT."); return; }
        if (!isValidPhone(phone)) { warn("SĐT không hợp lệ (10–11 chữ số)."); return; }
        if (email != null && !isValidEmail(email)) { warn("Email không hợp lệ."); return; }
        if (dep == null)       { warn("Vui lòng chọn Khoa."); return; }
        try {
            fee = ValidationUtils.parseFee(txtFee.getText());
        } catch (NumberFormatException ex) {
            warn("Phí khám không hợp lệ (phải là số ≥ 0).");
            return;
        }

        DoctorDTO dto = new DoctorDTO();
        dto.setFullName(fullName);
        dto.setGender(gender);
        dto.setDateOfBirth(dob);
        dto.setPhone(phone);
        dto.setEmail(email);
        dto.setAddress(address);
        dto.setConsultationFee(fee);
        dto.setDepartmentId(dep.getId());
        dto.setDoctorStatus(status);
        dto.setNotes(notes);

        try {
            DoctorDTO saved = doctorService.create(dto); // service lo transaction/unique check
            info("Đã lưu bác sĩ: " + saved.getFullName() + " (ID=" + saved.getId() + ")");
            clearForm();
            refreshTable();
        } catch (IllegalArgumentException dup) { // ví dụ SĐT/Email trùng
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }
    }

    private void initGender() {
        cbGender.getItems().setAll(Gender.values()); // MALE, FEMALE, OTHER

        cbGender.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });

        cbGender.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });

        cbGender.setConverter(new StringConverter<>() {
            @Override public String toString(Gender g) { return genderVi(g); }
            @Override public Gender fromString(String s) { return null; }
        });

        cbGender.setPromptText("Chọn giới tính");
    }


    private static String genderVi(Gender g) {
        if (g == null) return "";
        return switch (g) {
            case MALE   -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER  -> "Khác";
        };
    }

    private void clearForm() {
        txtFullName.clear();
        cbGender.getSelectionModel().clearSelection();
        dpDob.setValue(null);
        txtPhone.clear();
        txtEmail.clear();
        cbDepartmentForm.getSelectionModel().clearSelection();
        txtFee.clear();
        txtAddress.clear();
        if (txtNotes != null) txtNotes.clear();
    }

    private void initDepartmentCombo()
    {
        var deps = departmentService.findAll();
        cbDepartmentForm.setItems(FXCollections.observableArrayList(deps));
        cbDepartmentForm.setConverter(new StringConverter<>() {
        @Override public String toString(DepartmentDTO d) { return d == null ? "" : d.getName(); }
        @Override public DepartmentDTO fromString(String s) { return null; } // không dùng
        });
        cbDepartmentForm.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(DepartmentDTO d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "" : d.getName());
            }
        });

        cbDepartmentForm.setPromptText("Chọn khoa");
    }
    private void initDoctorStatus() {
        doctorStatus.getItems().setAll(DoctorStatus.values());

        doctorStatus.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(DoctorStatus st, boolean empty) {
                super.updateItem(st, empty);
                setText(empty || st == null ? "" : statusVi(st));
            }
        });

        doctorStatus.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DoctorStatus st, boolean empty) {
                super.updateItem(st, empty);
                setText(empty || st == null ? "" : statusVi(st));
            }
        });

        doctorStatus.setConverter(new StringConverter<>() {
            @Override public String toString(DoctorStatus st) { return statusVi(st); }
            @Override public DoctorStatus fromString(String s) { return null; }
        });

        doctorStatus.setPromptText("Chọn trạng thái");
    }

    private String statusVi(DoctorStatus st) {
        return switch (st) {
            case ACTIVE -> "Hoạt động";
            case INACTIVE -> "Ngưng hoạt động";
            case PENDING_APPROVAL -> "Chờ duyệt";
            case SUSPENDED -> "Đình chỉ";
            case ON_LEAVE -> "Tạm nghỉ";
        };
    }

    private void refreshTable() {
        var list = doctorService.findAll(); // cần method findAll() ở service
        tblDoctors.setItems(FXCollections.observableArrayList(list));
    }

    private void warn(String m){
        new Alert(Alert.AlertType.WARNING, m, ButtonType.OK).showAndWait();
    }
    private void info(String m){
        new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait();
    }
    private void showSystemError(Throwable ex){
        new Alert(Alert.AlertType.ERROR,
                "Đã xảy ra lỗi. Vui lòng thử lại.\nChi tiết (dev): " + ex.getMessage(),
                ButtonType.OK).showAndWait();
    }
    private Double parseDoubleSafe(String s){
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { warn("Phí khám không hợp lệ"); return null; }
    }

}
