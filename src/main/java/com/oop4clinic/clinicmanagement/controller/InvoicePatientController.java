package com.oop4clinic.clinicmanagement.controller;

import com.oop4clinic.clinicmanagement.dao.InvoicePatientDAO;
import com.oop4clinic.clinicmanagement.model.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class InvoicePatientController implements Initializable {
    @FXML private MenuItem infoPatient, recordPatient, billPatient, appointmentPatient, logoutPatient;
    @FXML private Button homeButton;

    @FXML private TableView<Invoice> billTable;
    @FXML private TableColumn<Invoice, Integer> orderNumberColumn;
    @FXML private TableColumn<Invoice, String> invoiceIdColumn;
    @FXML private TableColumn<Invoice, String> descriptionColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;
    @FXML private TableColumn<Invoice, Double> amountDueColumn;
    @FXML private TableColumn<Invoice, Double> amountPaidColumn;

    private final ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        customizeColumns();
        loadData();
        billTable.setItems(invoiceList);
    }

    private void setupTableColumns() {
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        amountPaidColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void customizeColumns() {
        statusColumn.setCellFactory(column -> new TableCell<Invoice, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Đã thanh toán đủ".equals(item)) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-alignment: center;");
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold; -fx-alignment: center;");
                    }
                }
            }
        });

        formatCurrencyColumn(amountDueColumn);
        formatCurrencyColumn(amountPaidColumn);
    }

    private void formatCurrencyColumn(TableColumn<Invoice, Double> column) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        column.setCellFactory(tc -> new TableCell<Invoice, Double>() {
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

        int currentPatientId = 1;
        List<Invoice> invoicesFromDb = InvoicePatientDAO.getInvoicesByPatientId(currentPatientId);
        invoiceList.addAll(invoicesFromDb);
    }

    // chuyen trang
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
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Logout.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
    @FXML void home(ActionEvent event) throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/com/oop4clinic/clinicmanagement/fxml/Book1.fxml"));
        Scene scene = homeButton.getScene();
        scene.setRoot(root);
    }
}