package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.service.UserService;
import com.oop4clinic.clinicmanagement.service.impl.AuthService;
import com.oop4clinic.clinicmanagement.util.ValidationUtils;
import com.oop4clinic.clinicmanagement.model.dto.DepartmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.DoctorDTO;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.DepartmentService;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import com.oop4clinic.clinicmanagement.service.impl.DepartmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.DoctorServiceImpl;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import static com.oop4clinic.clinicmanagement.util.ValidationUtils.*;

public class  DoctorManagementController {

    // ============================================================
    //  SERVICE
    // ============================================================
    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final DoctorService doctorService = new DoctorServiceImpl();
    private final UserService authService = new AuthService();

    // ============================================================
    //  FXML - FORM CHI TIẾT
    // ============================================================
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
    @FXML private Button btnEdit;
    @FXML private Button btnSave;
    @FXML private Button btnDeactivate; // chưa dùng nhưng vẫn để vì FXML có
    @FXML private Button btnCreateAccount;

    // ============================================================
    //  FXML - DANH SÁCH / LỌC
    // ============================================================
    @FXML private SplitPane split;
    @FXML private VBox detailPane;

    @FXML private TableView<DoctorDTO> tblDoctors;
    @FXML private TableColumn<DoctorDTO, String> colCode;
    @FXML private TableColumn<DoctorDTO, String> colFullName;
    @FXML private TableColumn<DoctorDTO, String> colDepartment;
    @FXML private TableColumn<DoctorDTO, String> colGender;
    @FXML private TableColumn<DoctorDTO, String> colStatus;
    @FXML private TableColumn<DoctorDTO, String> colPhone;
    @FXML private TableColumn<DoctorDTO, String> colEmail;
    @FXML private TableColumn<DoctorDTO, String> colFee;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<DepartmentDTO> cbDepartment;
    @FXML private ComboBox<DoctorStatus> cbActive;
    @FXML private Button btnFilter;
    @FXML private Button btnAdd;
    @FXML private Button btnImport;
    @FXML private Button btnExport;

    // ============================================================
    //  STATE
    // ============================================================
    private Integer selectedDoctorId;
    private enum FormMode { NONE, CREATE, EDIT }
    private FormMode currentMode = FormMode.NONE;
    private int detailPaneIndex = -1;

    // ============================================================
    //  INIT
    // ============================================================
    @FXML
    public void initialize() {
        initDepartmentCombos();
        initEnumCombos();
        initDoctorTable();

        // load lần đầu
        reloadAndShow();

        // nhớ index ban đầu của panel detail để add lại đúng chỗ
        detailPaneIndex = split.getItems().indexOf(detailPane);
        hideDetailPane();

        // trạng thái nút ban đầu
        btnEdit.setDisable(true);
        btnSave.setDisable(false);

        // enable/disable nút "Sửa" theo selection
        tblDoctors.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldSel, newSel) -> btnEdit.setDisable(newSel == null));
    }

    // ============================================================
    //  CLICK TRONG BẢNG
    // ============================================================
    @FXML
    private void onDoctorTableClicked(MouseEvent event) {
        DoctorDTO clicked = tblDoctors.getSelectionModel().getSelectedItem();
        if (clicked == null) return;

        Integer clickedId = clicked.getId();

        // Nếu đang EDIT đúng bác sĩ đó => toggle đóng panel
        if (isDetailPaneShowing()
                && currentMode == FormMode.EDIT
                && selectedDoctorId != null
                && selectedDoctorId.equals(clickedId)) {
            hideDetailPane();
            return;
        }

        // Load fresh detail từ service
        DoctorDTO detail = doctorService.findById(clickedId);
        fillFormFrom(detail);

        selectedDoctorId = clickedId;
        currentMode = FormMode.EDIT;

        showDetailPane();

        // EDIT mode
        btnEdit.setDisable(false);
        btnSave.setDisable(true);
    }

    // ============================================================
    //  CRUD HANDLERS
    // ============================================================
    @FXML
    private void onUpdateDoctor() {
        if (selectedDoctorId == null || currentMode != FormMode.EDIT) {
            warn("Chưa chọn bác sĩ để sửa.");
            return;
        }

        String err = validateFormOrWarn();
        if (err != null) { warn(err); return; }

        DoctorDTO dto;
        try {
            dto = buildDtoFromForm();
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
            return;
        }

        dto.setId(selectedDoctorId);

        try {
            DoctorDTO saved = doctorService.update(dto);
            info("Đã cập nhật bác sĩ: " + saved.getFullName());
            afterSaveOrUpdate();
        } catch (IllegalArgumentException dup) {
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }
    }

    @FXML
    private void onSaveDoctor() {
        if (currentMode != FormMode.CREATE) {
            warn("Hiện tại không ở chế độ thêm bác sĩ mới.");
            return;
        }

        String err = validateFormOrWarn();
        if (err != null) { warn(err); return; }

        DoctorDTO dto;
        try {
            dto = buildDtoFromForm();
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
            return;
        }

        try {
            DoctorDTO saved = doctorService.create(dto);

            authService.createOrResetDoctorAccount(saved.getId());

            info("Đã thêm bác sĩ: " + saved.getFullName());
            afterSaveOrUpdate();
        } catch (IllegalArgumentException dup) {
            warn(dup.getMessage());
        } catch (RuntimeException e) {
            showSystemError(e);
        }catch (Exception ex){
            showSystemError(ex);
        }
    }

    @FXML
    private void onCreateDoctorAccount() {

        if(selectedDoctorId == null || currentMode!=FormMode.EDIT) return;

        try
        {
            authService.createOrResetDoctorAccount(selectedDoctorId);
            info("Mật khẩu bác sĩ đặt thành công");
        }
        catch (Exception ex)
        {
            warn(ex.getMessage());
        }
    }

    @FXML
    private void onAddDoctor() {
        // nếu panel CREATE đang mở -> tắt
        if (isDetailPaneShowing() && currentMode == FormMode.CREATE) {
            hideDetailPane();
            return;
        }

        // chuyển sang chế độ CREATE mới
        clearForm();
        selectedDoctorId = null;
        currentMode = FormMode.CREATE;

        showDetailPane();

        btnEdit.setDisable(true);   // không "Sửa"
        btnSave.setDisable(false);  // cho phép "Lưu"
    }

    /** Sau khi thêm mới / cập nhật xong */
    private void afterSaveOrUpdate() {
        clearForm();
        selectedDoctorId = null;
        reloadAndShow();
        hideDetailPane();
    }

    // ============================================================
    //  FILTER
    // ============================================================
    @FXML
    private void onApplyFilterTop() {
        reloadAndShow();
    }

    @FXML
    private void onClearFilterTop() {
        txtSearch.clear();
        cbDepartment.getSelectionModel().clearSelection();
        cbActive.getSelectionModel().clearSelection();

        // reset placeholder cho combobox filter
        cbDepartment.setButtonCell(deptPlaceholderCell());
        cbActive.setButtonCell(statusPlaceholderCell());

        reloadAndShow();
    }

    /** Gọi service lấy danh sách theo filter + đổ vào bảng */
    private void reloadAndShow() {
        String keyword = trimOrNull(txtSearch.getText());

        DepartmentDTO dep = cbDepartment.getValue();
        Integer depId = (dep == null ? null : dep.getId());

        DoctorStatus st = cbActive.getValue();

        var result = doctorService.searchDoctors(keyword, depId, st);
        tblDoctors.setItems(FXCollections.observableArrayList(result));
    }

    // ============================================================
    //  UI INIT
    // ============================================================
    private void initDoctorTable() {
        colCode.setCellValueFactory(c -> {
            DoctorDTO dto = c.getValue();
            String prefix = codePrefixFromDepartmentName(dto.getDepartmentName());
            String code = (dto.getId() == null)
                    ? ""
                    : prefix + String.format("%03d", dto.getId());
            return wrap(code);
        });

        colFullName.setCellValueFactory(c -> wrap(c.getValue().getFullName()));
        colDepartment.setCellValueFactory(c -> wrap(c.getValue().getDepartmentName()));
        colGender.setCellValueFactory(c -> wrap(genderVi(c.getValue().getGender())));
        colStatus.setCellValueFactory(c -> wrap(statusVi(c.getValue().getDoctorStatus())));
        colPhone.setCellValueFactory(c -> wrap(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> wrap(c.getValue().getEmail()));
        colFee.setCellValueFactory(c -> {
            Double fee = c.getValue().getConsultationFee();
            return wrap(fee == null ? "—" : formatVnd(fee));
        });

        styleStatusColumn(colStatus);

        tblDoctors.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initDepartmentCombos() {
        var deps = FXCollections.observableArrayList(departmentService.findAll());

        // combo trong form (panel phải)
        cbDepartmentForm.setItems(deps);
        setupComboBox(
                cbDepartmentForm,
                d -> d == null ? "" : d.getName(),
                "Chọn khoa"
        );

        // combo filter (thanh trên)
        cbDepartment.setItems(deps);
        setupComboBox(
                cbDepartment,
                d -> d == null ? "" : d.getName(),
                null
        );
        cbDepartment.setButtonCell(deptPlaceholderCell());
    }

    /** init các combobox enum (Gender / DoctorStatus) */
    private void initEnumCombos() {
        // Gender (form)
        cbGender.getItems().setAll(Gender.values());
        setupComboBox(
                cbGender,
                ValidationUtils::genderVi,
                "Chọn giới tính"
        );

        // DoctorStatus (form)
        doctorStatus.getItems().setAll(DoctorStatus.values());
        setupComboBox(
                doctorStatus,
                ValidationUtils::statusVi,
                "Chọn trạng thái"
        );

        // DoctorStatus (filter)
        cbActive.setItems(FXCollections.observableArrayList(DoctorStatus.values()));
        setupComboBox(
                cbActive,
                ValidationUtils::statusVi,
                null
        );
        cbActive.setButtonCell(statusPlaceholderCell());
    }

    /**
     * Generic setup cho ComboBox<T>
     */
    private <T> void setupComboBox(
            ComboBox<T> combo,
            java.util.function.Function<T,String> toText,
            String promptText
    ) {
        combo.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toText.apply(item));
            }
        });

        combo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : toText.apply(item));
            }
        });

        combo.setConverter(new StringConverter<>() {
            @Override public String toString(T item) {
                return (item == null) ? "" : toText.apply(item);
            }
            @Override public T fromString(String s) { return null; }
        });

        if (promptText != null) {
            combo.setPromptText(promptText);
        }
    }

    private ListCell<DepartmentDTO> deptPlaceholderCell() {
        return new ListCell<>() {
            @Override protected void updateItem(DepartmentDTO d, boolean empty) {
                super.updateItem(d, empty);
                setText((empty || d == null) ? "Khoa" : d.getName());
            }
        };
    }

    private ListCell<DoctorStatus> statusPlaceholderCell() {
        return new ListCell<>() {
            @Override protected void updateItem(DoctorStatus st, boolean empty) {
                super.updateItem(st, empty);
                setText((empty || st == null) ? "Trạng thái" : statusVi(st));
            }
        };
    }

    // ============================================================
    //  SPLITPANE / DETAIL PANE
    // ============================================================
    private boolean isDetailPaneShowing() {
        return split.getItems().contains(detailPane);
    }

    private void showDetailPane() {
        if (!isDetailPaneShowing()) {
            if (detailPaneIndex >= 0 && detailPaneIndex <= split.getItems().size()) {
                split.getItems().add(detailPaneIndex, detailPane);
            } else {
                split.getItems().add(detailPane);
            }
        }
        Platform.runLater(() -> split.setDividerPositions(0.75));
    }

    private void hideDetailPane() {
        split.getItems().remove(detailPane);

        currentMode = FormMode.NONE;
        selectedDoctorId = null;

        btnEdit.setDisable(true);
        btnSave.setDisable(false);

        Platform.runLater(() -> split.setDividerPositions(1.0));
    }

    // ============================================================
    //  FORM BUILD / VALIDATE
    // ============================================================
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

    /** Dùng khi chuyển sang CREATE hoặc sau khi lưu xong */
    private void clearForm() {
        txtFullName.clear();
        cbGender.getSelectionModel().clearSelection();
        dpDob.setValue(null);
        txtPhone.clear();
        txtEmail.clear();
        txtFee.clear();
        txtAddress.clear();
        txtNotes.clear();
        doctorStatus.getSelectionModel().clearSelection();
        cbDepartmentForm.getSelectionModel().clearSelection();
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

        if (isBlank(fullName))                     return "Vui lòng nhập Họ tên.";
        if (gender == null)                        return "Vui lòng chọn Giới tính.";
        if (status == null)                        return "Vui lòng chọn Trạng thái.";
        if (!isValidDob(dob))                      return "Ngày sinh không hợp lệ.";
        if (isBlank(phone))                        return "Vui lòng nhập SĐT.";
        if (!isValidPhone(phone))                  return "SĐT không hợp lệ (10–11 chữ số).";
        if (email != null && !isValidEmail(email)) return "Email không hợp lệ.";
        if (dep == null)                           return "Vui lòng chọn Khoa.";

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

    private static ReadOnlyStringWrapper wrap(String s) {
        return new ReadOnlyStringWrapper(s == null ? "" : s);
    }

    // ============================================================
    //  TABLE CELL STYLE (STATUS)
    // ============================================================
    private void styleStatusColumn(TableColumn<DoctorDTO, String> colStatus) {
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String text, boolean empty) {
                super.updateItem(text, empty);
                if (empty || text == null || text.isBlank()) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(text);
                setStyle(switch (text) {
                    case "Hoạt động"       -> "-fx-text-fill:#1a7f37; -fx-font-weight:bold;";
                    case "Ngưng hoạt động" -> "-fx-text-fill:#b42318; -fx-font-weight:bold;";
                    case "Chờ duyệt"       -> "-fx-text-fill:#b78103; -fx-font-weight:bold;";
                    case "Đình chỉ"        -> "-fx-text-fill:#8a2be2; -fx-font-weight:bold;";
                    case "Tạm nghỉ"        -> "-fx-text-fill:#555; -fx-font-weight:bold;";
                    default                -> "";
                });
            }
        });
    }

    // ============================================================
    //  ALERT HELPERS
    // ============================================================
    private void warn(String message){
        showAlert(Alert.AlertType.WARNING,"Thiếu thông tin",message);
    }

    private void info(String message){
        showAlert(Alert.AlertType.INFORMATION,"Thành công",message);
    }

    private void showSystemError(Throwable ex){
        showAlert(
                Alert.AlertType.ERROR,
                "Lỗi hệ thống",
                "Đã xảy ra lỗi không mong muốn.\n\nVui lòng thử lại.\n\nChi tiết kỹ thuật: " + ex.getMessage()
        );
    }

    private void showAlert(Alert.AlertType type, String title, String body){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(body);
        styleDialog(alert);
        alert.showAndWait();
    }

    private void styleDialog(Alert alert){
        DialogPane pane = alert.getDialogPane();
        pane.setStyle(
            "-fx-font-size:13px;" +
            "-fx-background-color:linear-gradient(#ffffff,#f4f7ff);" +
            "-fx-background-radius:10;" +
            "-fx-padding:16;"
        );
    }
}
