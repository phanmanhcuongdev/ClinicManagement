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

    //===== Service =====
    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final DoctorService doctorService = new DoctorServiceImpl();

    //===== FXML fields =====
    @FXML private TextField txtFullName;
    @FXML private ComboBox<Gender> cbGender;
    @FXML private DatePicker dpDob;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<DepartmentDTO> cbDepartmentForm;
    @FXML private TextField txtFee;
    @FXML private TextArea txtAddress;
    @FXML private TextArea txtNotes;
    @FXML private ComboBox<DoctorStatus> doctorStatus;
    @FXML private SplitPane split;
    @FXML private Button btnEdit;
    @FXML private Button btnSave;
    @FXML private TableView<DoctorDTO> tblDoctors;
    @FXML private TableColumn<DoctorDTO, String> colCode;
    @FXML private TableColumn<DoctorDTO, String> colFullName;
    @FXML private TableColumn<DoctorDTO, String> colDepartment;
    @FXML private TableColumn<DoctorDTO, String> colGender;
    @FXML private TableColumn<DoctorDTO, String> colStatus;
    @FXML private TableColumn<DoctorDTO, String> colPhone;
    @FXML private TableColumn<DoctorDTO, String> colEmail;
    @FXML private TableColumn<DoctorDTO, String> colFee;

    //===== State =====
    private Integer selectedDoctorId;

    //===== Init =====
    @FXML
    public void initialize() {
        initDepartmentCombo();
        initGender();
        initDoctorStatus();
        holdPosition();
        initDoctorTable();
        refreshTable();
        btnEdit.setDisable(true);
        btnSave.setDisable(false);
    }

    //===== Events =====
    @FXML
    private void onDoctorTableClicked() {
        DoctorDTO row = tblDoctors.getSelectionModel().getSelectedItem();
        if (row == null) return;

        DoctorDTO detail = doctorService.findById(row.getId());
        selectedDoctorId = row.getId();
        fillFormFrom(detail);

        btnEdit.setDisable(false);
        btnSave.setDisable(true);
    }

    @FXML
    private void onUpdateDoctor() {
        if (selectedDoctorId == null) { warn("Chưa lựa chọn bác sĩ để sửa"); return; }

        String err = validateFormOrWarn();
        if (err != null) { warn(err); return; }

        DoctorDTO dto;
        try { dto = buildDtoFromForm(); }
        catch (IllegalArgumentException ex) { warn(ex.getMessage()); return; }

        dto.setId(selectedDoctorId);

        try {
            DoctorDTO saved = doctorService.update(dto);
            info("Đã sửa bác sĩ: " + saved.getFullName() + " (ID=" + saved.getId() + ")");
            clearForm();
            selectedDoctorId = null;
            refreshTable();
            btnEdit.setDisable(true);
            btnSave.setDisable(false);
        } catch (IllegalArgumentException dup) {
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }
    }

    @FXML
    private void onSaveDoctor() {
        String err = validateFormOrWarn();
        if (err != null) { warn(err); return; }

        DoctorDTO dto;
        try { dto = buildDtoFromForm(); }
        catch (IllegalArgumentException ex) { warn(ex.getMessage()); return; }

        try {
            DoctorDTO saved = doctorService.create(dto);
            info("Đã lưu bác sĩ: " + saved.getFullName() + " (ID=" + saved.getId() + ")");
            clearForm();
            refreshTable();
            btnEdit.setDisable(true);
            btnSave.setDisable(false);
        } catch (IllegalArgumentException dup) {
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }
    }

    //===== UI Init =====
    private void initDoctorTable() {
        colCode.setCellValueFactory(c -> {
            var dto = c.getValue();
            String prefix = codePrefixFromDepartmentName(dto.getDepartmentName());
            String code = (dto.getId() == null) ? "" : prefix + String.format("%03d", dto.getId());
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
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                if (empty || text == null || text.isBlank()) { setText(null); setStyle(""); return; }
                setText(text);
                switch (text) {
                    case "Hoạt động"       -> setStyle("-fx-text-fill:#1a7f37; -fx-font-weight:bold;");
                    case "Ngưng hoạt động" -> setStyle("-fx-text-fill:#b42318; -fx-font-weight:bold;");
                    case "Chờ duyệt"       -> setStyle("-fx-text-fill:#b78103; -fx-font-weight:bold;");
                    case "Đình chỉ"        -> setStyle("-fx-text-fill:#8a2be2; -fx-font-weight:bold;");
                    case "Tạm nghỉ"        -> setStyle("-fx-text-fill:#555; -fx-font-weight:bold;");
                    default                -> setStyle("");
                }
            }
        });
        colPhone.setCellValueFactory(c -> wrap(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> wrap(c.getValue().getEmail()));
        colFee.setCellValueFactory(c -> {
            Double fee = c.getValue().getConsultationFee();
            return wrap(fee == null ? "—" : formatVnd(fee));
        });

        tblDoctors.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initDepartmentCombo() {
        var deps = departmentService.findAll();
        cbDepartmentForm.setItems(FXCollections.observableArrayList(deps));
        cbDepartmentForm.setConverter(new StringConverter<>() {
            @Override public String toString(DepartmentDTO d) { return d == null ? "" : d.getName(); }
            @Override public DepartmentDTO fromString(String s) { return null; }
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
            @Override protected void updateItem(DoctorStatus st, boolean empty) {
                super.updateItem(st, empty);
                setText(empty || st == null ? "" : statusVi(st));
            }
        });
        doctorStatus.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(DoctorStatus st, boolean empty) {
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

    private void initGender() {
        cbGender.getItems().setAll(Gender.values());
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

    private void refreshTable() {
        var list = doctorService.findAll();
        tblDoctors.setItems(FXCollections.observableArrayList(list));
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


    //===== Helpers =====
    private void fillFormFrom(DoctorDTO d) {
        if (d == null) return;
        txtFullName.setText(d.getFullName());
        cbGender.setValue(d.getGender());
        dpDob.setValue(d.getDateOfBirth());
        txtPhone.setText(d.getPhone());
        txtEmail.setText(d.getEmail());
        txtFee.setText(d.getConsultationFee() == null ? "" : String.valueOf(d.getConsultationFee()));
        txtAddress.setText(d.getAddress());
        txtNotes.setText(d.getNotes());
        doctorStatus.setValue(d.getDoctorStatus());
        selectDepartmentById(d.getDepartmentId());
    }

    private DoctorDTO buildDtoFromForm() {
        Double fee;
        try { fee = ValidationUtils.parseFee(txtFee.getText()); }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Phí khám không hợp lệ (phải là số ≥ 0).");
        }

        DepartmentDTO dep = cbDepartmentForm.getValue();
        DoctorDTO dto = new DoctorDTO();
        dto.setFullName(trimOrNull(txtFullName.getText()));
        dto.setGender(cbGender.getValue());
        dto.setDateOfBirth(dpDob.getValue());
        dto.setPhone(trimOrNull(txtPhone.getText()));
        dto.setEmail(trimOrNull(txtEmail.getText()));
        dto.setAddress(trimOrNull(txtAddress.getText()));
        dto.setConsultationFee(fee);
        dto.setDepartmentId(dep == null ? null : dep.getId());
        dto.setDoctorStatus(doctorStatus.getValue());
        dto.setNotes(trimOrNull(txtNotes.getText()));
        return dto;
    }

    private String validateFormOrWarn() {
        String fullName = trimOrNull(txtFullName.getText());
        Gender gender = cbGender.getValue();
        var dob = dpDob.getValue();
        String phone = trimOrNull(txtPhone.getText());
        String email = trimOrNull(txtEmail.getText());
        DepartmentDTO dep = cbDepartmentForm.getValue();
        DoctorStatus status = doctorStatus.getValue();

        if (isBlank(fullName)) return "Vui lòng nhập Họ tên.";
        if (gender == null)    return "Vui lòng chọn Giới tính.";
        if (status == null)    return "Vui lòng chọn Trạng thái.";
        if (!isValidDob(dob))  return "Ngày sinh không hợp lệ.";
        if (isBlank(phone))    return "Vui lòng nhập SĐT.";
        if (!isValidPhone(phone)) return "SĐT không hợp lệ (10–11 chữ số).";
        if (email != null && !isValidEmail(email)) return "Email không hợp lệ.";
        if (dep == null)       return "Vui lòng chọn Khoa.";
        return null;
    }

    private void selectDepartmentById(Integer id) {
        if (id == null) { cbDepartmentForm.getSelectionModel().clearSelection(); return; }
        for (DepartmentDTO dep : cbDepartmentForm.getItems()) {
            if (id.equals(dep.getId())) { cbDepartmentForm.setValue(dep); break; }
        }
    }

    private static javafx.beans.property.ReadOnlyStringWrapper wrap(String s) {
        return new javafx.beans.property.ReadOnlyStringWrapper(s == null ? "" : s);
    }

    private static String formatVnd(double v) {
        return String.format("%,.0f đ", v).replace(',', '.');
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

    private static String genderVi(Gender g) {
        if (g == null) return "";
        return switch (g) {
            case MALE -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER -> "Khác";
        };
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

    private void warn(String m){ new Alert(Alert.AlertType.WARNING, m, ButtonType.OK).showAndWait(); }
    private void info(String m){ new Alert(Alert.AlertType.INFORMATION, m, ButtonType.OK).showAndWait(); }
    private void showSystemError(Throwable ex){
        new Alert(Alert.AlertType.ERROR,
                "Đã xảy ra lỗi. Vui lòng thử lại.\nChi tiết (dev): " + ex.getMessage(),
                ButtonType.OK).showAndWait();
    }
}
