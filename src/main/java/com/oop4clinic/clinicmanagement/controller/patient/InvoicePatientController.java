package com.oop4clinic.clinicmanagement.controller.patient;

import com.oop4clinic.clinicmanagement.model.dto.InvoiceDTO;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;
import com.oop4clinic.clinicmanagement.service.InvoiceService;
import com.oop4clinic.clinicmanagement.service.impl.InvoiceServiceImpl;
import com.oop4clinic.clinicmanagement.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class InvoicePatientController implements Initializable {
    @FXML private MenuItem infoPatient, recordPatient, billPatient, appointmentPatient, logoutPatient;
    @FXML private Button homeButton;
    @FXML private TableView<InvoiceDTO> billTable;
    @FXML private TableColumn<InvoiceDTO, Integer> orderNumberColumn;
    @FXML private TableColumn<InvoiceDTO, String> descriptionColumn; // Ánh xạ tới 'details'
    @FXML private TableColumn<InvoiceDTO, InvoiceStatus> statusColumn; // Ánh xạ tới 'status'
    @FXML private TableColumn<InvoiceDTO, Double> amountDueColumn; // Ánh xạ tới 'total'
    @FXML private TableColumn<InvoiceDTO, Double> amountPaidColumn; // Ánh xạ tới 'total'

    private final InvoiceService invoiceService = new InvoiceServiceImpl();
    private final ObservableList<InvoiceDTO> invoiceList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        customizeColumns();
        loadData();
        billTable.setItems(invoiceList);
    }

    private void setupTableColumns() {
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        descriptionColumn.setCellFactory(tc -> new TableCell<InvoiceDTO, String>() {
            private final Text text = new Text();

            {
                text.setWrappingWidth(0);
                text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                text.setTextAlignment(TextAlignment.LEFT);
                setAlignment(Pos.CENTER_LEFT);
                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(item);
                }
            }
        });
        // tbao
        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        amountPaidColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        orderNumberColumn.setCellValueFactory(cellData -> null);
        orderNumberColumn.setCellFactory(col -> new TableCell<InvoiceDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                    setStyle("-fx-alignment: center;");
                }
            }
        });
    }

    private void customizeColumns() {
        statusColumn.setCellFactory(column -> new TableCell<InvoiceDTO, InvoiceStatus>() {
            @Override
            protected void updateItem(InvoiceStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String statusDisplay;
                    if (item == InvoiceStatus.PAID) {
                        statusDisplay = "Đã thanh toán đủ";
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-alignment: center;");
                    } else {
                        statusDisplay = (item == InvoiceStatus.UNPAID) ? "Chưa thanh toán" : "Đã hủy";
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-alignment: center;");
                    }
                    setText(statusDisplay);
                }
            }
        });

        // Định dạng cột tiền tệ
        formatCurrencyColumn(amountDueColumn);
        formatCurrencyColumn(amountPaidColumn);
    }

    private void formatCurrencyColumn(TableColumn<InvoiceDTO, Double> column) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        column.setCellFactory(tc -> new TableCell<InvoiceDTO, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
    }

    private void loadData() {
        invoiceList.clear();
        var currentPatient = UserSession.getCurrentPatient();
        if (currentPatient == null) {
            System.err.println("⚠ Không tìm thấy bệnh nhân trong session, không thể tải hóa đơn!");
            return;
        }
        int currentPatientId = currentPatient.getId();
        List<InvoiceDTO> invoicesFromService = invoiceService.getInvoicesByPatientId(currentPatientId);
        if (invoicesFromService != null) {
            invoiceList.addAll(invoicesFromService);
        } else {
            System.err.println("Không tải được dữ liệu hóa đơn. Kiểm tra console để biết thêm chi tiết.");
        }
    }

    @FXML void handleInfo(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InfoPatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleRecord(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/MedicalRecord.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleBill(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/InvoicePatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleAppointment(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/AppointmentPatient.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void handleLogout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Login.fxml"));
        UserSession.clear();
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Booking1.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
}