package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class PatientFormDialogController {

    // ===== FXML bindings =====
    @FXML private TextField txtId; // hidden trong FXML
    @FXML private TextField txtFullName;
    @FXML private ComboBox<Gender> cboGender;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private TextField txtCccd;
    @FXML private TextField txtInsuranceCode;

    @FXML private Label lblError;
    @FXML private Button btnCancel;

    // ===== State =====
    private PatientDTO editing;   // != null -> edit mode
    private PatientDTO result;    // trả về cho màn chính (null nếu Hủy)

    // ===== Init =====
    @FXML
    private void initialize() {
        // Bơm enum (tránh <items> trong FXML)
        initGender();

        // Định dạng nhập nhanh
        setBasicFormatters();
    }

    private void initGender() {
        cboGender.getItems().setAll(Gender.values());
        cboGender.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });
        cboGender.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });
        cboGender.setConverter(new StringConverter<>() {
            @Override public String toString(Gender g) { return genderVi(g); }
            @Override public Gender fromString(String s) { return null; }
        });
        cboGender.setPromptText("Chọn giới tính");
    }

    private static String genderVi(Gender g) {
        if (g == null) return "";
        return switch (g) {
            case MALE -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER -> "Khác";
        };
    }

    // ===== API gọi từ màn danh sách khi SỬA =====
    public void setEditing(PatientDTO dto) {
        this.editing = dto;
        if (dto == null) return;

        if (dto.getId() != null) txtId.setText(String.valueOf(dto.getId()));
        txtFullName.setText(nvl(dto.getFullName()));
        if (dto.getGender() != null) cboGender.getSelectionModel().select(dto.getGender());
        dpDateOfBirth.setValue(dto.getDateOfBirth());
        txtPhone.setText(nvl(dto.getPhone()));
        txtEmail.setText(nvl(dto.getEmail()));
        txtAddress.setText(nvl(dto.getAddress()));
        txtCccd.setText(nvl(dto.getCccd()));
        txtInsuranceCode.setText(nvl(dto.getInsuranceCode()));
    }

    /** Lấy DTO sau khi dialog đóng; null nếu người dùng Hủy */
    public PatientDTO getResult() {
        return result;
    }

    // ===== Actions =====
    @FXML
    private void onSave() {
        lblError.setText("");

        // Lấy & làm sạch input
        String fullName  = tidy(txtFullName.getText());
        Gender gender    = cboGender.getValue();
        LocalDate dob    = dpDateOfBirth.getValue();
        String phone     = tidy(txtPhone.getText());
        String email     = tidy(txtEmail.getText());
        String address   = tidy(txtAddress.getText());
        String cccd      = tidy(txtCccd.getText());
        String insurance = tidy(txtInsuranceCode.getText());

        // ---- Validate (đã sửa kiểu return void) ----
        if (fullName.isEmpty()) {
            err("Họ và tên là bắt buộc.");
            return;
        }
        if (gender == null) {
            err("Vui lòng chọn giới tính.");
            return;
        }
        if (dob == null) {
            err("Vui lòng chọn ngày sinh.");
            return;
        }
        if (dob.isAfter(LocalDate.now())) {
            err("Ngày sinh không hợp lệ.");
            return;
        }
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            err("SĐT không hợp lệ.");
            return;
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            err("Email không hợp lệ.");
            return;
        }
        if (!cccd.isEmpty() && !cccd.matches("^\\d{9,12}$")) {
            err("CCCD không hợp lệ (9–12 chữ số).");
            return;
        }

        // Map sang DTO; nếu edit thì giữ id cũ
        PatientDTO dto = (editing != null) ? editing : new PatientDTO();
        if (editing != null) dto.setId(editing.getId());
        dto.setFullName(fullName);
        dto.setGender(gender);
        dto.setDateOfBirth(dob);
        dto.setPhone(emptyToNull(phone));
        dto.setEmail(emptyToNull(email));
        dto.setAddress(emptyToNull(address));
        dto.setCccd(emptyToNull(cccd));
        dto.setInsuranceCode(emptyToNull(insurance));

        this.result = dto;
        close();
    }

    @FXML
    private void onCancel() {
        this.result = null;
        close();
    }

    private void close() {
        Stage st = (Stage) btnCancel.getScene().getWindow();
        st.close();
    }

    // ===== Helpers =====
    private void err(String msg) { lblError.setText(msg); }
    private static String nvl(String s) { return s == null ? "" : s; }
    private static String tidy(String s) { return s == null ? "" : s.trim(); }
    private static String emptyToNull(String s) { return (s == null || s.isBlank()) ? null : s; }

    private static boolean isValidPhone(String s) {
        // Cho phép 0 hoặc +84, tổng 9–11 chữ số (nới lỏng)
        return s.matches("^(0|\\+?84)\\d{8,10}$");
    }
    private static boolean isValidEmail(String s) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(s).matches();
    }

    private void setBasicFormatters() {
        // SĐT: chỉ cho số và + ; tối đa 15 ký tự
        txtPhone.setTextFormatter(new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > 15) return null;
            if (t.isEmpty() || t.matches("[0-9+]*")) return change;
            return null;
        }));
        // CCCD: chỉ số, tối đa 12
        txtCccd.setTextFormatter(new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.length() > 12) return null;
            if (t.isEmpty() || t.matches("\\d*")) return change;
            return null;
        }));
    }
}
