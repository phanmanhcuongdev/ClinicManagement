package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.model.entity.Patient;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.service.PatientService;
import com.oop4clinic.clinicmanagement.service.impl.PatientServiceImpl;
import com.oop4clinic.clinicmanagement.service.query.PageRequest;
import com.oop4clinic.clinicmanagement.service.query.PatientFilter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;

public class PatientManagementController {

    // =========================================================================
    //  DEPENDENCY / STATE
    // =========================================================================

    private final PatientService patientService = new PatientServiceImpl();

    // Paging state
    private int currentPage = 0;
    private int pageSize    = 20; // sẽ được tính động dựa theo chiều cao bảng
    private boolean sizingLock = false; // tránh loop khi auto-resize gọi loadPage()

    // =========================================================================
    //  FXML BINDINGS
    // =========================================================================

    @FXML private Label lblStatus;
    @FXML private Label lblCount;

    @FXML private TableView<PatientDTO> tblPatients;
    @FXML private TableColumn<PatientDTO, String>   colFullName;
    @FXML private TableColumn<PatientDTO, LocalDate> colDob;
    @FXML private TableColumn<PatientDTO, String>   colPhone;
    @FXML private TableColumn<PatientDTO, String>   colEmail;
    @FXML private TableColumn<PatientDTO, String>   colInsurance;
    @FXML private TableColumn<PatientDTO, String>   colAddress;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<Gender> cboGenderFilter;
    @FXML private DatePicker dpDobFrom;
    @FXML private DatePicker dpDobTo;

    @FXML private Pagination pagination;

    @FXML private Button btnEdit;
    @FXML private MenuItem miOpen;

    // =========================================================================
    //  LIFECYCLE / INIT
    // =========================================================================

    @FXML
    private void initialize() {
        initTableColumns();
        initGenderCombo();
        initPagination();
        initAdaptivePageSize();

        // Trang đầu tiên
        currentPage = 0;
        pagination.setCurrentPageIndex(0);

        tblPatients.getSelectionModel()
                        .selectedItemProperty()
                                .addListener((obs,oldSel,newSel) -> {
                                    boolean hasSelection = newSel !=null;
                                    btnEdit.setDisable(!hasSelection);
                                    miOpen.setDisable(!hasSelection);
                                });

        loadPage();
    }

    /**
     * Gán cellValueFactory cho từng cột TableView
     */
    private void initTableColumns() {
        colFullName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName())
        );
        colDob.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDateOfBirth())
        );
        colPhone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone())
        );
        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail())
        );
        colInsurance.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getInsuranceCode())
        );
        colAddress.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress())
        );

        // cố định chiều cao mỗi dòng để tính được pageSize động
        tblPatients.setFixedCellSize(32);
    }

    /**
     * Cài đặt combobox filter giới tính (hiển thị "Nam", "Nữ", ...)
     */
    private void initGenderCombo() {
        cboGenderFilter.getItems().setAll(Gender.values());

        // item trong dropdown
        cboGenderFilter.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });

        // item hiển thị trên button chính
        cboGenderFilter.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Gender g, boolean empty) {
                super.updateItem(g, empty);
                setText(empty || g == null ? "" : genderVi(g));
            }
        });

        // converter -> dùng cho toString()
        cboGenderFilter.setConverter(new StringConverter<>() {
            @Override public String toString(Gender g) { return genderVi(g); }
            @Override public Gender fromString(String s) { return null; }
        });

        cboGenderFilter.setPromptText("Chọn giới tính");
    }

    /**
     * Pagination: khi đổi trang thì loadPage()
     */
    private void initPagination() {
        pagination.currentPageIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            currentPage = newIdx.intValue();
            loadPage();
        });
    }

    /**
     * Auto điều chỉnh pageSize dựa theo chiều cao thực tế của TableView.
     * - Khi bảng render xong (skin ready)
     * - Khi user resize cửa sổ (heightProperty đổi)
     */
    private void initAdaptivePageSize() {
        tblPatients.heightProperty().addListener((obs, oldH, newH) -> recalcPageSizeAndReload());
        tblPatients.skinProperty().addListener((obs, oldSkin, newSkin) -> recalcPageSizeAndReload());
    }

    // =========================================================================
    //  PAGING / LOADING DATA
    // =========================================================================

    /**
     * Tính lại pageSize dựa trên chiều cao bảng hiện tại.
     * Nếu pageSize thay đổi -> reset về trang 0 và tải lại.
     */
    private void recalcPageSizeAndReload() {
        if (sizingLock) return; // chống vòng lặp vô hạn

        var header = tblPatients.lookup("TableHeaderRow");
        if (header == null) {
            // header chưa có (UI chưa render xong), lần sau gọi lại sẽ tính
            return;
        }

        double headerHeight     = header.getBoundsInParent().getHeight();
        double totalTableHeight = tblPatients.getHeight();
        double availableForRows = totalTableHeight - headerHeight;

        double rowHeight = tblPatients.getFixedCellSize();
        if (rowHeight <= 0) rowHeight = 32; // fallback

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

    /**
     * Gọi service để lấy dữ liệu trang hiện tại + cập nhật UI
     */
    private void loadPage() {
        // build filter từ form
        PatientFilter filter = buildFilterFromForm();

        // gọi service
        var result = patientService.findByFilter(
                filter,
                new PageRequest(currentPage, pageSize)
        );

        // Nếu filter mới làm giảm số trang -> chỉnh currentPage cho hợp lệ
        if (result.getTotalPages() > 0 && currentPage >= result.getTotalPages()) {
            currentPage = result.getTotalPages() - 1;
            if (currentPage < 0) currentPage = 0;

            result = patientService.findByFilter(
                    filter,
                    new PageRequest(currentPage, pageSize)
            );
        }
        if (currentPage < 0) currentPage = 0;

        // Đổ data lên bảng
        tblPatients.getItems().setAll(result.getContent());

        // Cập nhật Pagination
        int totalPages = Math.max(result.getTotalPages(), 1);
        if (pagination.getPageCount() != totalPages) {
            pagination.setPageCount(totalPages);
        }
        if (pagination.getCurrentPageIndex() != currentPage) {
            pagination.setCurrentPageIndex(currentPage);
        }

        // Cập nhật label thống kê
        int fromIdx = result.getContent().isEmpty()
                ? 0
                : currentPage * pageSize + 1;

        int toIdx = currentPage * pageSize + result.getContent().size();

        lblCount.setText(String.format("%d–%d / %d",
                fromIdx, toIdx, result.getTotalElements()));

        lblStatus.setText(String.format(
                "Trang %d/%d (mỗi trang ~%d dòng)",
                currentPage + 1,
                Math.max(result.getTotalPages(), 1),
                pageSize
        ));
    }

    /**
     * Gom input filter từ form UI thành 1 object PatientFilter để truyền xuống service
     */
    private PatientFilter buildFilterFromForm() {
        return new PatientFilter(
                txtSearch.getText(),
                cboGenderFilter.getValue(),
                dpDobFrom.getValue(),
                dpDobTo.getValue()
        );
    }

    // =========================================================================
    //  UI ACTIONS (BUTTON / CLICK / FILTER)
    // =========================================================================

    @FXML
    private void applyFilter() {
        // Khi người dùng nhấn "Lọc"
        currentPage = 0;
        pagination.setCurrentPageIndex(0);
        loadPage();
    }

    @FXML
    private void clearFilter() {
        // Khi người dùng nhấn "Xóa lọc"
        txtSearch.clear();
        cboGenderFilter.getSelectionModel().clearSelection();
        dpDobFrom.setValue(null);
        dpDobTo.setValue(null);

        currentPage = 0;
        pagination.setCurrentPageIndex(0);
        loadPage();

        lblStatus.setText("Đã xoá bộ lọc.");
    }

    @FXML
    private void Refresh() {
        // Nút reload cứng
        currentPage = 0;
        pagination.setCurrentPageIndex(0);
        loadPage();
    }

    @FXML
    private void onPatientTableClicked(javafx.scene.input.MouseEvent event) {
        // Chỉ xử lý nếu double click chuột trái
        if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY
                && event.getClickCount() == 2) {

            PatientDTO row = tblPatients.getSelectionModel().getSelectedItem();
            if (row == null) return;

            openPatientDialog(row);


            // nếu chỉ click 1 lần (clickCount == 1):
            // -> không làm gì, chỉ chọn dòng thôi
            // nếu click phải:
            // -> JavaFX tự lo contextMenu như bình thường
        }
    }


    @FXML
    private void onEditButtonClicked()
    {
        PatientDTO row = tblPatients.getSelectionModel().getSelectedItem();
        if(row == null) return;
        openPatientDialog(row);
    }

    @FXML
    private void onOpenClicked()
    {
        PatientDTO row = tblPatients.getSelectionModel().getSelectedItem();
        if(row == null) return;
        openPatientDialog(row);
    }


    @FXML
    private void addPatient() {
        // Nút "Thêm bệnh nhân"
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/PatientFormDialog.fxml")
            );
            Parent root = loader.load();

            PatientFormDialogController dialogCtl = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Thêm bệnh nhân");
            stage.initOwner(tblPatients.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Lấy dữ liệu từ dialog
            PatientDTO dto = dialogCtl.getResult();
            if (dto == null) return;

            try {
                PatientDTO created = patientService.create(dto);

                currentPage = 0;
                pagination.setCurrentPageIndex(0);
                loadPage();

                lblStatus.setText("Đã thêm bệnh nhân: " + created.getFullName());
            } catch (IllegalStateException ex) {
                // ví dụ trùng phone/email/cccd/insuranceCode
                new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR,
                        "Không thể tạo bệnh nhân:\n" + ex.getMessage()
                ).showAndWait();
            }

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Không thể mở form thêm bệnh nhân:\n" + e.getMessage()
            ).showAndWait();
        }
    }

    // =========================================================================
    //  SUPPORT / UTIL
    // =========================================================================


    private void openPatientDialog(PatientDTO selected)
    {
        if(selected == null) return;

        try
        {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/PatientFormDialog.fxml")
            );
            Parent root = loader.load();

            PatientFormDialogController dialogController = loader.getController();
            dialogController.setEditing(selected);

            Stage stage = new Stage();
            stage.setTitle("Thông tin bệnh nhân");
            stage.initOwner(tblPatients.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            PatientDTO updated = dialogController.getResult();
            if (updated != null) {
                patientService.update(updated);
                loadPage();
                lblStatus.setText("Đã cập nhật: " + updated.getFullName());
            }

        }catch(IOException e)
        {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Không thể mở thông tin bệnh nhân:\n" + e.getMessage()
            ).showAndWait();
        }
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
