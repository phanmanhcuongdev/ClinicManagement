package com.oop4clinic.clinicmanagement.controller.doctor;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.dto.PatientDTO;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class PatientDetailDialogController {

    @FXML private Label patientNameLabel;
    @FXML private Label patientGenderLabel;
    @FXML private Label patientDobLabel;
    @FXML private Label patientPhoneLabel;

    @FXML private TableView<MedicalRecordDTO> historyTable;
    @FXML private TableColumn<MedicalRecordDTO, String> colHistoryDate;
    @FXML private TableColumn<MedicalRecordDTO, String> colHistorySymptoms;
    @FXML private TableColumn<MedicalRecordDTO, String> colHistoryDiagnosis;
    @FXML private TableColumn<MedicalRecordDTO, String> colHistoryPrescription;
    @FXML private TableColumn<MedicalRecordDTO, String> colHistoryNotes;
    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        colHistoryDate.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getCreatedAt();
            return new ReadOnlyStringWrapper(time != null ? time.format(dateTimeFormatter) : "");
        });
        colHistorySymptoms.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getSymptoms())
        );
        colHistoryDiagnosis.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDiagnosis())
        );
        colHistoryPrescription.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getPrescription())
        );
        colHistoryNotes.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getNotes())
        );

        setupColumnTooltip(colHistoryPrescription);
        setupColumnTooltip(colHistoryNotes);
        setupColumnTooltip(colHistorySymptoms);
        setupColumnTooltip(colHistoryDiagnosis);
        setupColumnTooltip(colHistoryNotes);
    }
    private <T> void setupColumnTooltip(TableColumn<MedicalRecordDTO, String> column) {

        column.setCellFactory(new Callback<TableColumn<MedicalRecordDTO, String>, TableCell<MedicalRecordDTO, String>>() {
            @Override
            public TableCell<MedicalRecordDTO, String> call(TableColumn<MedicalRecordDTO, String> param) {

                return new TableCell<MedicalRecordDTO, String>() {
                    private final Tooltip tooltip = new Tooltip();
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(item);
                            tooltip.setText(item);
                            setTooltip(tooltip);
                        }
                    }
                };
            }
        });
    }

    public void setData(PatientDTO patient) {
        if (patient == null) return;
        patientNameLabel.setText(patient.getFullName());
        patientGenderLabel.setText(patient.getGender() != null ? patient.getGender().toString() : "");
        patientDobLabel.setText(patient.getDateOfBirth() != null ? patient.getDateOfBirth().format(dateFormatter) : "");
        patientPhoneLabel.setText(patient.getPhone());

        try {
            List<MedicalRecordDTO> records = medicalRecordService.getMedicalRecordsForPatient(patient.getId());
            historyTable.setItems(FXCollections.observableArrayList(records));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}