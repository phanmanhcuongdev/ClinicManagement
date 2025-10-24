package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.impl.PatientServiceImpl;
import com.oop4clinic.clinicmanagement.service.query.PageRequest;
import com.oop4clinic.clinicmanagement.service.query.PatientFilter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;

public class PatientManagementController {

    // =========================================================================
    //  SERVICE + STATE
    // =========================================================================
    private final PatientService patientService = new PatientServiceImpl();

    private int  currentPage = 0;
    private int  pageSize    = 20;    // sẽ điều chỉnh động
    private boolean sizingLock = false; // tránh vòng lặp khi recalcPageSizeAndReload()

    // =========================================================================
    //  FXML
    // =========================================================================
    @FXML private Label lblStatus;
    @FXML private Label lblCount;

    @FXML private TableView<PatientDTO> tblPatients;
    @FXML private TableColumn<PatientDTO, String>    colFullName;
    @FXML private TableColumn<PatientDTO, LocalDate> colDob;
    @FXML private TableColumn<PatientDTO, String>    colPhone;
    @FXML private TableColumn<PatientDTO, String>    colEmail;
    @FXML private TableColumn<PatientDTO, String>    colInsurance;
    @FXML private TableColumn<PatientDTO, String>    colAddress;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<Gender> cboGenderFilter;
    @FXML private DatePicker dpDobFrom;
    @FXML private DatePicker dpDobTo;

    @FXML private Pagination pagination;

    @FXML private Button btnEdit;
    @FXML private MenuItem miOpen;

    // =========================================================================
    //  INIT LIFECYCLE
    // =========================================================================
    @FXML
    private void initialize() {
        initTableColumns();
        initGenderFilterCombo();
        initPaginationListener();
        initAdaptivePageSize();

        // set trang đầu
        currentPage = 0;
        pagination.setCurrentPageIndex(0);

        // bật/tắt nút Edit/Open theo selection
        tblPatients.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean has = newSel != null;
                    btnEdit.setDisable(!has);
                    miOpen.setDisable(!has);
                });

        // double click mở dialog
        tblPatients.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 2) {
                editSelected();
            }
        });

        loadPage(); // load lần đầu
    }

    // =========================================================================
    //  TABLE INIT
    // =========================================================================
    private void initTableColumns() {
        colFullName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName())
        );
        colDob.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDateOfBirth())
        );
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone())
        );
        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );
        colInsurance.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getInsuranceCode())
        );
        colAddress.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getAddress())
        );

        // cố định chiều cao mỗi dòng để tính pageSize
        tblPatients.setFixedCellSize(32);
    }

    // =========================================================================
    //  FILTER UI INIT
    // =========================================================================
    private void initGenderFilterCombo() {
        cboGenderFilter.getItems().setAll(Gender.values());

        cboGenderFilter.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });

        cboGenderFilter.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });

        cboGenderFilter.setConverter(new StringConverter<>() {
            @Override public String toString(Gender g) { return genderVi(g); }
            @Override public Gender fromString(String s) { return null; }
        });

        cboGenderFilter.setPromptText("Chọn giới tính");
    }

    // =========================================================================
    //  PAGINATION INIT
    // =========================================================================
    private void initPaginationListener() {
        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            currentPage = newIdx.intValue();
            loadPage();
        });
    }

    private void initAdaptivePageSize() {
        // khi bảng render xong hoặc bị resize -> tính lại pageSize
        tblPatients.heightProperty().addListener((obs, oldH, newH) -> recalcPageSizeAndReload());
        tblPatients.skinProperty().addListener((obs, oldSkin, newSkin) -> recalcPageSizeAndReload());
    }

    // =========================================================================
    //  PAGE LOAD LOGIC
    // =========================================================================
    private void recalcPageSizeAndReload() {
        if (sizingLock) return;

        var header = tblPatients.lookup("TableHeaderRow");
        if (header == null) return; // bảng chưa render xong

        double headerHeight     = header.getBoundsInParent().getHeight();
        double totalTableHeight = tblPatients.getHeight();
        double availableForRows = totalTableHeight - headerHeight;

        double rowHeight = tblPatients.getFixedCellSize();
        if (rowHeight <= 0) rowHeight = 32;

        int rowsFit = (int) Math.floor(availableForRows / rowHeight);
        if (rowsFit < 1) rowsFit = 1;

        if (rowsFit != pageSize) {
            pageSize = rowsFit;
            currentPage = 0;
            pagination.setCurrentPageIndex(0);

            sizingLock = true;
            loadPage();
            sizingLock = false;
        }
    }

    private void loadPage() {
        PatientFilter filter = buildFilterFromForm();

        var result = patientService.findByFilter(filter, new PageRequest(currentPage, pageSize));

        // nếu currentPage quá lớn sau khi lọc -> kéo về trang cuối hợp lệ
        if (result.getTotalPages() > 0 && currentPage >= result.getTotalPages()) {
            currentPage = Math.max(result.getTotalPages() - 1, 0);

            result = patientService.findByFilter(filter, new PageRequest(currentPage, pageSize));
        }
        if (currentPage < 0) currentPage = 0;

        // data cho bảng
        tblPatients.getItems().setAll(result.getContent());

        // cập nhật Pagination
        int totalPages = Math.max(result.getTotalPages(), 1);
        if (pagination.getPageCount() != totalPages) {
            pagination.setPageCount(totalPages);
        }
        if (pagination.getCurrentPageIndex() != currentPage) {
            pagination.setCurrentPageIndex(currentPage);
        }

        // label thống kê
        int fromIdx = result.getContent().isEmpty() ? 0 : currentPage * pageSize + 1;
        int toIdx   = currentPage * pageSize + result.getContent().size();

        lblCount.setText(String.format("%d–%d / %d",
                fromIdx, toIdx, result.getTotalElements()));

        lblStatus.setText(String.format(
                "Trang %d/%d (mỗi trang ~%d dòng)",
                currentPage + 1,
                Math.max(result.getTotalPages(), 1),
                pageSize
        ));
    }

    private PatientFilter buildFilterFromForm() {
        return new PatientFilter(
                txtSearch.getText(),
                cboGenderFilter.getValue(),
                dpDobFrom.getValue(),
                dpDobTo.getValue()
        );
    }

    // =========================================================================
    //  UI ACTIONS (BUTTON / MENU / CLICK)
    // =========================================================================
    @FXML
    private void applyFilter() {
        resetToFirstPageAndLoad(null);
    }

    @FXML
    private void clearFilter() {
        txtSearch.clear();
        cboGenderFilter.getSelectionModel().clearSelection();
        dpDobFrom.setValue(null);
        dpDobTo.setValue(null);

        resetToFirstPageAndLoad("Đã xoá bộ lọc.");
    }

    @FXML
    private void Refresh() {
        resetToFirstPageAndLoad(null);
    }

    @FXML
    private void onEditButtonClicked() {
        editSelected();
    }

    @FXML
    private void onOpenClicked() {
        editSelected();
    }

    private void editSelected() {
        PatientDTO row = tblPatients.getSelectionModel().getSelectedItem();
        if (row == null) return;

        PatientDTO updated = openPatientDialog(row);
        if (updated != null) {
            patientService.update(updated);
            loadPage();
            lblStatus.setText("Đã cập nhật: " + updated.getFullName());
        }
    }

    @FXML
    private void addPatient() {
        PatientDTO created = openNewPatientDialog();
        if (created == null) return;

        try {
            PatientDTO saved = patientService.create(created);
            resetToFirstPageAndLoad("Đã thêm bệnh nhân: " + saved.getFullName());
        } catch (IllegalStateException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Không thể tạo bệnh nhân:\n" + ex.getMessage()
            ).showAndWait();
        }
    }

    // =========================================================================
    //  DIALOG HELPERS
    // =========================================================================
    /**
     * Mở dialog ở chế độ EDIT/VIEW với dữ liệu sẵn có.
     * Trả về dto cập nhật (nếu user bấm Lưu) hoặc null (nếu huỷ).
     */
    private PatientDTO openPatientDialog(PatientDTO selected) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/PatientFormDialog.fxml")
            );
            Parent root = loader.load();

            PatientFormDialogController dialogCtl = loader.getController();
            dialogCtl.setEditing(selected);

            Stage stage = modalStage("Thông tin bệnh nhân", root);
            stage.showAndWait();

            return dialogCtl.getResult();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Không thể mở thông tin bệnh nhân:\n" + e.getMessage()
            ).showAndWait();
            return null;
        }
    }

    /**
     * Mở dialog ở chế độ CREATE mới.
     * Trả về dto mới (nếu user bấm Lưu) hoặc null (nếu huỷ).
     */
    private PatientDTO openNewPatientDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/PatientFormDialog.fxml")
            );
            Parent root = loader.load();

            PatientFormDialogController dialogCtl = loader.getController();

            Stage stage = modalStage("Thêm bệnh nhân", root);
            stage.showAndWait();

            return dialogCtl.getResult();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Không thể mở form thêm bệnh nhân:\n" + e.getMessage()
            ).showAndWait();
            return null;
        }
    }

    private Stage modalStage(String title, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initOwner(tblPatients.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        return stage;
    }

    // =========================================================================
    //  SMALL UTIL
    // =========================================================================
    private void resetToFirstPageAndLoad(String statusMsg) {
        currentPage = 0;
        pagination.setCurrentPageIndex(0);
        loadPage();
        if (statusMsg != null) lblStatus.setText(statusMsg);
    }

    private static String genderVi(Gender g) {
        if (g == null) return "";
        return switch (g) {
            case MALE   -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER  -> "Khác";
        };
    }


}
