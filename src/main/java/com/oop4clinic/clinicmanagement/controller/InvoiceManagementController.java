package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.model.dto.InvoiceDTO;
import com.oop4clinic.clinicmanagement.model.entity.Invoice;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;
import com.oop4clinic.clinicmanagement.service.impl.InvoiceServiceImpl;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class InvoiceManagementController implements Initializable {
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button refreshButton;
    @FXML private TableView<InvoiceDTO> invoiceTable;
    @FXML private TableColumn<InvoiceDTO, Integer> invoiceIdCol;
    @FXML private TableColumn<InvoiceDTO, String> patientNameCol;
    @FXML private TableColumn<InvoiceDTO, String> doctorNameCol;
    @FXML private TableColumn<InvoiceDTO, LocalDateTime> createdAtCol;
    @FXML private TableColumn<InvoiceDTO, Double> totalAmountCol;
    @FXML private TableColumn<InvoiceDTO, String> statusCol;
    @FXML private TableColumn<InvoiceDTO ,Void> actionCol;

    private ObservableList<InvoiceDTO> masterData = FXCollections.observableArrayList();
    private InvoiceServiceImpl invoiceService = new InvoiceServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupInvoiceTableColumns();
        loadInvoiceData();
        setupSearchFilter();
    }

    private void setupInvoiceTableColumns() {
        // set up các cột cho bảng
        invoiceIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject()
        );

        patientNameCol.setCellValueFactory(cellData -> {
            InvoiceDTO invoiceDTO = cellData.getValue();
            String patientName = (invoiceDTO != null && invoiceDTO.getPatientName() != null)
                    ? invoiceDTO.getPatientName() : "N/A";
            return new SimpleStringProperty(patientName);
        });

        createdAtCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getCreatedAt())
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        createdAtCol.setCellFactory(column -> new TableCell<InvoiceDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        totalAmountCol.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject()
        );

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        totalAmountCol.setCellFactory(column -> new TableCell<InvoiceDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                }
            }
        });

        statusCol.setCellValueFactory(cellData ->
        {
            InvoiceDTO invoiceDTO = cellData.getValue();
            String text;
            if (invoiceDTO.getStatus() == InvoiceStatus.PAID) {
                text = "Đã thanh toán";
            } else {
                text = "Chưa thanh toán";
            }
            return new SimpleStringProperty(text);
        });

        actionCol.setCellFactory(param -> new TableCell<InvoiceDTO, Void>() {
            private final Button payButton = new Button("Thanh Toán");

            { // Khối khởi tạo cho nút
                payButton.setOnAction(event -> {
                    InvoiceDTO invoiceDTO = getTableView().getItems().get(getIndex());
                    handlePaymentAction(invoiceDTO);
                });
                // payButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    InvoiceDTO invoice = getTableView().getItems().get(getIndex());

                    if (!"PAID".equalsIgnoreCase(invoice.getStatus().name())) { // Điều chỉnh "PAID" nếu cần
                        setGraphic(payButton);
                        setAlignment(Pos.CENTER);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });


        invoiceTable.setPlaceholder(new Label("Không có hóa đơn nào."));
    }

    private void handlePaymentAction(InvoiceDTO invoiceDTO) {

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Bạn có chắc chắn muốn đánh dấu hóa đơn này là ĐÃ THANH TOÁN?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = false;
            try {
                success = invoiceService.updateInvoiceStatus(invoiceDTO.getId(), InvoiceStatus.PAID);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }

            if (success) {
                loadInvoiceData(); // Reload all invoices

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành Công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Hóa đơn #" + invoiceDTO.getId() + " đã được đánh dấu thanh toán thành công.");
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Thất Bại");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Không thể cập nhật trạng thái hóa đơn #" + invoiceDTO.getId() + ". Vui lòng thử lại.");
                errorAlert.showAndWait();
            }
        } else {
            System.out.println("Payment cancelled for invoice ID: " + invoiceDTO.getId());
        }
    }

    public boolean showConfirmAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void setupSearchFilter() {
        FilteredList<InvoiceDTO> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(invoice -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return invoice.getId().toString().toLowerCase().contains(keyword)
                        || invoice.getPatientName().toLowerCase().contains(keyword);
            });
        });


        SortedList<InvoiceDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(invoiceTable.comparatorProperty());

        invoiceTable.setItems(sortedData);
    }

    @FXML
    public void handleRefreshAction(ActionEvent event) {
        searchField.clear();
        masterData.setAll(invoiceService.getAll());
    }


    private void loadInvoiceData() {

        List<InvoiceDTO> invoiceList = invoiceService.getAll();

        if (invoiceList != null) {
            masterData = FXCollections.observableArrayList(invoiceList);
        } else {
            masterData = FXCollections.observableArrayList(); // Empty list if null
        }

        invoiceTable.setItems(masterData);
    }
}