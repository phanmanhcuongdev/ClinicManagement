package com.oop4clinic.clinicmanagement.controller.doctor;

import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.DoctorServiceImpl;
import com.oop4clinic.clinicmanagement.util.ValidationUtils;
import javafx.collections.FXCollections;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

import static com.oop4clinic.clinicmanagement.util.ValidationUtils.*;

public class DoctorProfileController {
    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final DoctorService doctorService = new DoctorServiceImpl();
    @FXML
    private TextField txtFullName;
    @FXML private ComboBox<Gender> cbGender;
    @FXML private DatePicker dpDob;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<DepartmentDTO> cbDepartmentForm;
    @FXML private TextField txtFee;
    @FXML private TextArea txtAddress;
    @FXML private TextArea txtNotes;
    @FXML private Button btnSave;
    private User loggedInDoctor;
    private DoctorDTO currentDoctorDTO;

    @FXML
    public void initialize() {
        initDepartmentCombo();
        initGenderCombo();

        cbDepartmentForm.setDisable(true);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/oop4clinic/clinicmanagement/fxml/MenuDoctor.fxml"
            ));
            Parent menuRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(menuRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể quay lại Menu chính: " + e.getMessage());
        }
    }
    public void setLoggedInDoctor(User doctor) {
        this.loggedInDoctor = doctor;
        if (this.loggedInDoctor != null) {
            loadDoctorProfile();
        } else {
            warn("Lỗi: Không thể tải thông tin bác sĩ.");
            btnSave.setDisable(true);
        }
    }

    private void loadDoctorProfile() {
        try {
            this.currentDoctorDTO = doctorService.findById(loggedInDoctor.getId());
            if (this.currentDoctorDTO != null) {
                fillFormFrom(this.currentDoctorDTO);
            } else {
                warn("Không tìm thấy hồ sơ bác sĩ với ID: " + loggedInDoctor.getId());
                btnSave.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSystemError(e);
            btnSave.setDisable(true);
        }
    }
    @FXML
    private void onSaveProfile() {
        if (this.currentDoctorDTO == null || this.loggedInDoctor == null) {
            warn("Không có thông tin bác sĩ để cập nhật.");
            return;
        }

        String err = validateFormOrWarn();
        if (err != null) {
            warn(err);
            return;
        }

        DoctorDTO dtoFromForm;
        try {
            dtoFromForm = buildDtoFromForm();
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
            return;
        }


        dtoFromForm.setId(this.currentDoctorDTO.getId());
        dtoFromForm.setDepartmentId(this.currentDoctorDTO.getDepartmentId());

        dtoFromForm.setDoctorStatus(this.currentDoctorDTO.getDoctorStatus());


        try {
            DoctorDTO saved = doctorService.update(dtoFromForm);
            info("Đã cập nhật thông tin cá nhân thành công.");

            this.currentDoctorDTO = saved;
            fillFormFrom(saved);
        } catch (IllegalArgumentException dup) {
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }
    }


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
        selectDepartmentById(d.getDepartmentId());
    }

    private DoctorDTO buildDtoFromForm() {
        Double fee;
        try {
            fee = ValidationUtils.parseFee(txtFee.getText());
        } catch (NumberFormatException ex) {
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

        dto.setNotes(trimOrNull(txtNotes.getText()));
        return dto;
    }

    private String validateFormOrWarn() {
        String fullName = trimOrNull(txtFullName.getText());
        Gender gender = cbGender.getValue();
        var dob = dpDob.getValue();
        String phone = trimOrNull(txtPhone.getText());
        String email = trimOrNull(txtEmail.getText());

        if (isBlank(fullName)) return "Vui lòng nhập Họ tên.";
        if (gender == null) return "Vui lòng chọn Giới tính.";
        if (!isValidDob(dob)) return "Ngày sinh không hợp lệ.";
        if (isBlank(phone)) return "Vui lòng nhập SĐT.";
        if (!isValidPhone(phone)) return "SĐT không hợp lệ (10–11 chữ số).";
        if (email != null && !isValidEmail(email)) return "Email không hợp lệ.";


        return null;
    }

    private void selectDepartmentById(Integer id) {
        if (id == null) {
            cbDepartmentForm.getSelectionModel().clearSelection();
            return;
        }
        for (DepartmentDTO dep : cbDepartmentForm.getItems()) {
            if (id.equals(dep.getId())) {
                cbDepartmentForm.setValue(dep);
                break;
            }
        }
    }

    private void initDepartmentCombo() {
        List<DepartmentDTO> deps = departmentService.findAll();
        cbDepartmentForm.setItems(FXCollections.observableArrayList(deps));
        setupComboBox(
                cbDepartmentForm,
                d -> d == null ? "" : d.getName(),
                "Chọn khoa"
        );
    }

    private void initGenderCombo() {
        cbGender.getItems().setAll(Gender.values());
        setupComboBox(
                cbGender,
                ValidationUtils::genderVi,
                "Chọn giới tính"
        );
    }

    private <T> void setupComboBox(
            ComboBox<T> combo,
            java.util.function.Function<T, String> toText,
            String promptText
    ) {
        combo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toText.apply(item));
            }
        });

        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toText.apply(item));
            }
        });

        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(T item) {
                return (item == null) ? "" : toText.apply(item);
            }
            @Override
            public T fromString(String s) {
                return null;
            } // Not needed
        });

        if (promptText != null) {
            combo.setPromptText(promptText);
        }
    }
    private void warn(String message) {
        showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", message);
    }

    private void info(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Thành công", message);
    }

    private void showSystemError(Throwable ex) {
        showAlert(
                Alert.AlertType.ERROR,
                "Lỗi hệ thống",
                "Đã xảy ra lỗi không mong muốn.\nChi tiết: " + ex.getMessage()
        );
    }

    private void showAlert(Alert.AlertType type, String title, String body) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(body);
        alert.showAndWait();
    }
}
