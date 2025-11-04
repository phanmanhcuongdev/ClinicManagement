package com.oop4clinic.clinicmanagement.controller.admin;

import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl; // Assume you have this service
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class EditMedicalRecordController {

    @FXML private Label patientNameLabel;
    @FXML private Label doctorNameLabel;
    @FXML private Label createdAtLabel;
    @FXML private TextArea symptomsTextArea;
    @FXML private TextArea diagnosisTextArea;
    @FXML private TextArea prescriptionTextArea;
    @FXML private TextArea notesTextArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private MedicalRecordDTO currentRecord;
    private MedicalRecordServiceImpl medicalRecordServiceImpl = new MedicalRecordServiceImpl(); // Instance of your service

    public void setMedicalRecordToEdit(MedicalRecordDTO record) {
        this.currentRecord = record;
        populateFields();
    }

    private void populateFields() {
        if (currentRecord == null) {
            // Handle error - shouldn't happen if called correctly
            System.err.println("Error: MedicalRecord is null in Edit dialog.");
            closeDialog(); // Close if no data
            return;
        }

        // Set non-editable labels
        patientNameLabel.setText(currentRecord.getPatientName() != null ? currentRecord.getPatientName() : "N/A");
        doctorNameLabel.setText(currentRecord.getDoctorName() != null ? currentRecord.getDoctorName(): "N/A");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        createdAtLabel.setText(currentRecord.getCreatedAt() != null ? currentRecord.getCreatedAt().format(formatter) : "N/A");

        // Set editable text areas
        symptomsTextArea.setText(currentRecord.getSymptoms());
        diagnosisTextArea.setText(currentRecord.getDiagnosis());
        prescriptionTextArea.setText(currentRecord.getPrescription());
        notesTextArea.setText(currentRecord.getNotes());
    }


    @FXML
    void handleSaveAction(ActionEvent event) {
        if (currentRecord == null) {
            return;
        }

        String updatedSymptoms = symptomsTextArea.getText();
        String updatedDiagnosis = diagnosisTextArea.getText();
        String updatedPrescription = prescriptionTextArea.getText();
        String updatedNotes = notesTextArea.getText();

        if (updatedSymptoms == null || updatedSymptoms.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Triệu chứng không được để trống.");
            return;
        }
        if (updatedDiagnosis == null || updatedDiagnosis.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Chẩn đoán không được để trống.");
            return;
        }

        // Update the MedicalRecord object
        currentRecord.setSymptoms(updatedSymptoms);
        currentRecord.setDiagnosis(updatedDiagnosis);
        currentRecord.setPrescription(updatedPrescription);
        currentRecord.setNotes(updatedNotes);

        // 4. Call service to save changes
        boolean success = medicalRecordServiceImpl.updateMedicalRecord(currentRecord); // Create this method in service/DAO

        // 5. Provide feedback and close
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Cập nhật hồ sơ bệnh án thành công.");
            closeDialog();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi Cập Nhật", "Không thể lưu thay đổi vào cơ sở dữ liệu.");
        }
    }


    @FXML
    void handleCancelAction(ActionEvent event) {
        closeDialog();
    }


    private void closeDialog() {
        // Get the stage (window) associated with the cancel button and close it
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}