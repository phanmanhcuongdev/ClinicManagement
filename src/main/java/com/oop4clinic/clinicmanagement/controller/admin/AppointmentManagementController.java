package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.lang.annotation.ElementType;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentManagementController implements Initializable {

    @FXML private TableView<AppointmentDTO> appointmentTable;
    @FXML private TableColumn<AppointmentDTO, Integer> appointmentIdCol;
    @FXML private TableColumn<AppointmentDTO, String> patientNameCol;
    @FXML private TableColumn<AppointmentDTO, String> doctorNameCol;
    @FXML private TableColumn<AppointmentDTO, String> departmentNameCol;
    @FXML private TableColumn<AppointmentDTO, LocalDateTime> startTimeCol;
    @FXML private TableColumn<AppointmentDTO, String > statusCol;
    @FXML private TableColumn<AppointmentDTO, String> reasonCol;
    @FXML private TextField searchField;
    @FXML private ComboBox<AppointmentStatus> statusFilterComboBox;
    @FXML private Button refreshButton;

    private AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();
    private ObservableList<AppointmentDTO> masterData = FXCollections.observableArrayList();
    private FilteredList<AppointmentDTO> filteredData; // Make FilteredList a field

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupAppointmentTableColumns();
        populateStatusFilter();
        setupFiltering();
        loadAppointmentData();
    }

    private void setupAppointmentTableColumns() {
        appointmentIdCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        patientNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(getPatientNameSafe(cellData.getValue())));
        doctorNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(getDoctorNameSafe(cellData.getValue())));
        departmentNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(getDepartmentNameSafe(cellData.getValue())));
        startTimeCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartTime()));
        reasonCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReason()));

        statusCol.setCellValueFactory(cellData ->
        {
            AppointmentDTO appointment = cellData.getValue();
            String text = convertStatus(appointment.getStatus());
            return new SimpleStringProperty(text);
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        startTimeCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });

        appointmentTable.setPlaceholder(new Label("Không có lịch hẹn nào."));
    }

    private void populateStatusFilter() {
        statusFilterComboBox.getItems().add(null); // Add null for "All" option
        statusFilterComboBox.getItems().add(AppointmentStatus.CONFIRMED);
        statusFilterComboBox.getItems().add(AppointmentStatus.COMPLETED);
        statusFilterComboBox.getItems().add(AppointmentStatus.CANCELED);
        statusFilterComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(AppointmentStatus status) {
                return status == null ? "Tất cả" : convertStatus(status); // Display "Tất cả" for null
            }
            @Override
            public AppointmentStatus fromString(String string) {
                return null; // Not needed for filtering display
            }
        });
        statusFilterComboBox.getSelectionModel().selectFirst(); // Select "Tất cả" initially
    }

    private String convertStatus(AppointmentStatus a){
        if(a == AppointmentStatus.COMPLETED) return "Đã hoàn thành";
        else if( a == AppointmentStatus.CONFIRMED) return "Đã xác nhận";
        else if(a == AppointmentStatus.CANCELED) return "Đã hủy";
        return "";
    }

    private void setupFiltering() {
        filteredData = new FilteredList<>(masterData, p -> true);

        // Listener for search field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        // Listener for combo box selection
        statusFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        SortedList<AppointmentDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(appointmentTable.comparatorProperty());
        appointmentTable.setItems(sortedData);
    }

    private void applyFilters() {
        String searchText = searchField.getText();
        AppointmentStatus selectedStatus = statusFilterComboBox.getValue();

        filteredData.setPredicate(appointment -> {
            boolean statusMatch = (selectedStatus == null) || (appointment.getStatus() == selectedStatus);

            if (searchText == null || searchText.isEmpty() || searchText.isBlank()) {
                return statusMatch; // Only filter by status if search is empty
            }

            if (!statusMatch) return false; // If status doesn't match, no need to check text

            String lowerCaseFilter = searchText.toLowerCase().trim();
            boolean textMatch = (getPatientNameSafe(appointment).toLowerCase().contains(lowerCaseFilter)) ||
                    (getDoctorNameSafe(appointment).toLowerCase().contains(lowerCaseFilter)) ||
                    (appointment.getReason() != null && appointment.getReason().toLowerCase().contains(lowerCaseFilter));
            // Add ID search if desired: || String.valueOf(appointment.getId()).contains(lowerCaseFilter)

            return textMatch;
        });
    }


    private void loadAppointmentData() {
        List<AppointmentDTO> appointmentList = appointmentService.getAll();
        masterData.clear();
        if (appointmentList != null) {
            masterData.addAll(appointmentList);
        }
    }

    @FXML
    void handleRefreshAction(ActionEvent event) {
        System.out.println("Refresh button clicked");
        searchField.clear();
        statusFilterComboBox.getSelectionModel().selectFirst(); // Reset filter to "All"
        loadAppointmentData();
    }

    // --- Helper methods ---
    private String getPatientNameSafe(AppointmentDTO appointment) {
        return (appointment != null && appointment.getPatientName() != null) ? appointment.getPatientName() : "";
    }

    private String getDoctorNameSafe(AppointmentDTO appointment) {
        return (appointment != null && appointment.getDoctorName() != null) ? appointment.getDoctorName() : "";
    }

    private String getDepartmentNameSafe(AppointmentDTO appointment) {
        return (appointment != null && appointment.getDepartmentName() != null) ? appointment.getDepartmentName(): "";
    }
}