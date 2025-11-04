package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.util.ValidationUtils;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class DepartmentManagementController {

    // ================== SERVICE ==================
    private final DepartmentService departmentService = new DepartmentServiceImpl();

    // ================== STATE ==================
    private enum Mode { NONE, CREATE, EDIT }
    private Mode currentMode = Mode.NONE;
    private Integer currentDeptId = null;

    // ================== HEADER / STATUS ==================
    @FXML private Label lblStatus;
    @FXML private Label lblDeptCount;

    // ================== TABLE (LEFT) ==================
    @FXML private TableView<DepartmentDTO> tblDepartments;
    @FXML private TableColumn<DepartmentDTO, Integer> colDeptId;
    @FXML private TableColumn<DepartmentDTO, String>  colDeptName;
    @FXML private TableColumn<DepartmentDTO, String>  colDeptBaseFee;

    @FXML private Button btnAddNew;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    @FXML private Button btnReload;

    // ================== FORM (RIGHT) ==================
    @FXML private Label lblIdCaption;
    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtBaseFee;
    @FXML private TextArea  txtDescription;
    @FXML private ListView<String> lstDoctorsInDept;

    // ========================================================
    // INIT
    // ========================================================
    @FXML
    private void initialize() {
        initTableColumns();
        setupSelectionListener();
        loadAllDepartments();
        enterNoneMode(); // protect form by default
    }

    private void initTableColumns() {
        colDeptId.setCellValueFactory(cd ->
                new SimpleObjectProperty<>(cd.getValue().getId())
        );

        colDeptName.setCellValueFactory(cd ->
                new SimpleStringProperty(safeStr(cd.getValue().getName()))
        );

        colDeptBaseFee.setCellValueFactory(cd -> {
            Double fee = cd.getValue().getBaseFee();
            String display = (fee == null)
                    ? "—"
                    : ValidationUtils.formatVnd(fee);
            return new SimpleStringProperty(display);
        });

        tblDepartments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupSelectionListener() {
        tblDepartments.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel == null) return;
                    showDepartment(newSel); // switch to EDIT mode
                });
    }

    // ========================================================
    // LOAD DATA
    // ========================================================
    private void loadAllDepartments() {
        List<DepartmentDTO> depts = departmentService.findAll();

        tblDepartments.getItems().setAll(depts);

        lblDeptCount.setText(depts.size() + " khoa");
        lblStatus.setText("Đã tải danh sách khoa.");

        // Sau khi reload, quay về trạng thái NONE
        enterNoneMode();
    }

    // ========================================================
    // SHOW / FILL FORM (DTO)
    // ========================================================
    private void showDepartment(DepartmentDTO dto) {
        if (dto == null) return;

        currentMode = Mode.EDIT;
        currentDeptId = dto.getId();

        // Đổ dữ liệu lên form
        txtId.setText(safeStr(dto.getId()));
        txtName.setText(safeStr(dto.getName()));
        txtBaseFee.setText(dto.getBaseFee() == null ? "" : String.valueOf(dto.getBaseFee()));
        txtDescription.setText(safeStr(dto.getDescription()));

        // Danh sách bác sĩ
        lstDoctorsInDept.getItems().clear();
        if (dto.getDoctorNames() != null) {
            lstDoctorsInDept.getItems().addAll(dto.getDoctorNames());
        }

        lblStatus.setText("Đang chỉnh sửa khoa ID=" + dto.getId());

        applyModeToForm();
    }

    // ========================================================
    // BUTTON HANDLERS
    // ========================================================

    @FXML
    private void onReload() {
        loadAllDepartments();
    }

    @FXML
    private void onAddNew() {
        enterCreateMode();
    }

    @FXML
    private void onSave() {
        // validate form
        String err = validateForm();
        if (err != null) {
            warn(err);
            return;
        }

        // build DTO từ form
        DepartmentDTO formDto = buildDtoFromForm();

        try {
            if (currentMode == Mode.CREATE) {
                DepartmentDTO created = departmentService.create(formDto);
                info("Đã thêm khoa: " + safeStr(created.getName()));
            } else if (currentMode == Mode.EDIT) {
                if (currentDeptId == null) {
                    warn("Thiếu ID khoa để cập nhật.");
                    return;
                }
                DepartmentDTO updated = departmentService.update(currentDeptId, formDto);
                info("Đã cập nhật khoa: " + safeStr(updated.getName()));
            } else {
                warn("Không ở chế độ tạo/sửa.");
                return;
            }

            loadAllDepartments(); // reload table + reset form

        } catch (IllegalArgumentException dup) {
            // ví dụ: duplicate name
            warn(dup.getMessage());
        } catch (RuntimeException ex) {
            error("Không thể lưu khoa:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        if (currentMode != Mode.EDIT || currentDeptId == null) {
            warn("Chưa chọn khoa để xoá.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xoá");
        confirm.setHeaderText("Bạn có chắc muốn xoá khoa này?");
        confirm.setContentText("Khoa ID=" + currentDeptId + " sẽ bị xoá.");
        var rs = confirm.showAndWait();
        if (rs.isEmpty() || rs.get() != ButtonType.OK) {
            return;
        }

        try {
            departmentService.deleteById(currentDeptId);
            info("Đã xoá khoa ID=" + currentDeptId);
            loadAllDepartments();
        } catch (RuntimeException ex) {
            error("Không thể xoá khoa:\n" + ex.getMessage());
        }
    }

    // ========================================================
    // FORM MODE CONTROL
    // ========================================================
    private void enterNoneMode() {
        currentMode = Mode.NONE;
        currentDeptId = null;

        clearForm();
        lblStatus.setText("Chưa chọn khoa.");

        applyModeToForm();
    }

    private void enterCreateMode() {
        currentMode = Mode.CREATE;
        currentDeptId = null;

        clearForm();
        lblStatus.setText("Thêm khoa mới.");

        applyModeToForm();
    }

    /**
     * Đồng bộ UI (enable/disable field & button, ẩn/hiện ID)
     * theo currentMode.
     */
    private void applyModeToForm() {
        boolean canEditFields = (currentMode == Mode.CREATE || currentMode == Mode.EDIT);
        boolean isEditMode    = (currentMode == Mode.EDIT);

        // vùng nhập liệu chính
        txtName.setDisable(!canEditFields);
        txtBaseFee.setDisable(!canEditFields);
        txtDescription.setDisable(!canEditFields);

        // ID: luôn readonly, và chỉ hiện khi đang EDIT
        txtId.setDisable(true);
        txtId.setVisible(isEditMode);
        lblIdCaption.setVisible(isEditMode);

        // ListView bác sĩ luôn chỉ xem
        lstDoctorsInDept.setDisable(true);

        // Buttons
        btnSave.setDisable(!canEditFields);
        btnDelete.setDisable(!isEditMode);
        // btnAddNew / btnReload luôn bật dùng được

        // Tô nền khác để nhìn thấy "đang bị khoá"
        String bg = canEditFields ? "white" : "#f0f2f8";
        txtName.setStyle("-fx-background-color:" + bg + ";");
        txtBaseFee.setStyle("-fx-background-color:" + bg + ";");
        txtDescription.setStyle("-fx-background-color:" + bg + ";");
    }

    private void clearForm() {
        txtId.clear();
        txtName.clear();
        txtBaseFee.clear();
        txtDescription.clear();
        lstDoctorsInDept.getItems().clear();
    }

    // ========================================================
    // BUILD / VALIDATE (DTO)
    // ========================================================
    private DepartmentDTO buildDtoFromForm() {
        DepartmentDTO dto = new DepartmentDTO();

        dto.setName(ValidationUtils.trimOrNull(txtName.getText()));

        // baseFee parse
        String feeRaw = ValidationUtils.trimOrNull(txtBaseFee.getText());
        Double feeVal = null;
        if (feeRaw != null && !feeRaw.isBlank()) {
            try {
                feeVal = Double.parseDouble(feeRaw);
                if (feeVal < 0) throw new NumberFormatException("negative");
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Phí khám phải là số >= 0.");
            }
        }
        dto.setBaseFee(feeVal);

        dto.setDescription(ValidationUtils.trimOrNull(txtDescription.getText()));

        // doctorNames không chỉnh ở đây

        return dto;
    }

    private String validateForm() {
        String name = ValidationUtils.trimOrNull(txtName.getText());
        String fee  = ValidationUtils.trimOrNull(txtBaseFee.getText());

        if (ValidationUtils.isBlank(name)) {
            return "Vui lòng nhập Tên khoa.";
        }

        if (fee != null) {
            try {
                double v = Double.parseDouble(fee);
                if (v < 0) return "Phí khám mặc định phải >= 0.";
            } catch (NumberFormatException ex) {
                return "Phí khám mặc định không hợp lệ.";
            }
        }

        return null;
    }

    // ========================================================
    // ALERT HELPERS
    // ========================================================
    private void warn(String msg) {
        showAlert(Alert.AlertType.WARNING, "Cảnh báo", msg);
    }

    private void info(String msg) {
        showAlert(Alert.AlertType.INFORMATION, "Thành công", msg);
    }

    private void error(String msg) {
        showAlert(Alert.AlertType.ERROR, "Lỗi", msg);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ========================================================
    // UTIL
    // ========================================================
    private static String safeStr(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }
}
