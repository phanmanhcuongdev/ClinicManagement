package com.oop4clinic.clinicmanagement.controller.doctor;

import com.oop4clinic.clinicmanagement.model.dto.AppointmentDTO;
import com.oop4clinic.clinicmanagement.model.dto.MedicalRecordDTO;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.AppointmentStatus;
import com.oop4clinic.clinicmanagement.service.AppointmentService;
import com.oop4clinic.clinicmanagement.service.MedicalRecordService;
import com.oop4clinic.clinicmanagement.service.impl.AppointmentServiceImpl;
import com.oop4clinic.clinicmanagement.service.impl.MedicalRecordServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
public class AddMedicalRecordDialogController {
    @FXML private TextField patientNameField;
    @FXML private TextField symptomsField;
    @FXML private TextField diagnosisField;
    @FXML private TextField prescriptionField;
    @FXML private TextArea notesField;
    @FXML private CheckBox completeCheckBox;
    private MedicalRecordDTO currentRecord;
    private AppointmentDTO currentAppointment;
    private boolean isEditMode = false;

    private User loggedInDoctor;

    public void setLoggedInDoctor(User doctor) {
        this.loggedInDoctor = doctor;

    }



    private final AppointmentService appointmentService = new AppointmentServiceImpl();
    private final MedicalRecordService medicalRecordService = new MedicalRecordServiceImpl();

    private MedicalRecordDTO savedRecord;
    private boolean completed = false;
    @FXML
    public void initialize() {

    }

    public void setData(AppointmentDTO appointment, MedicalRecordDTO record) {
        this.currentAppointment = appointment;
        this.patientNameField.setText(appointment.getPatient().getFullName());
        this.patientNameField.setEditable(false);
        boolean appointmentIsCompleted = (appointment.getStatus() == AppointmentStatus.COMPLETED);

        if (record != null) {

            this.isEditMode = true;
            this.currentRecord = record;
            symptomsField.setText(record.getSymptoms());
            diagnosisField.setText(record.getDiagnosis());
            prescriptionField.setText(record.getPrescription());
            notesField.setText(record.getNotes());
        } else {

            this.isEditMode = false;
            this.currentRecord = new MedicalRecordDTO();
        }
        symptomsField.setDisable(appointmentIsCompleted);
        diagnosisField.setDisable(appointmentIsCompleted);
        prescriptionField.setDisable(appointmentIsCompleted);
        notesField.setDisable(appointmentIsCompleted);
        completeCheckBox.setDisable(appointmentIsCompleted);
        completeCheckBox.setSelected(appointmentIsCompleted);
    }
    private MedicalRecordDTO performSave() {
        String symptoms = symptomsField.getText();
        String diagnosis = diagnosisField.getText();
        if (symptoms.isEmpty() || diagnosis.isEmpty()) {
            showAlert("Vui lòng nhập Triệu chứng và Chẩn đoán!");
            return null;
        }
        currentRecord.setSymptoms(symptoms);
        currentRecord.setDiagnosis(diagnosis);
        currentRecord.setPrescription(prescriptionField.getText());
        currentRecord.setNotes(notesField.getText());
        try {
            MedicalRecordDTO saved;
            if (isEditMode) {
                saved = medicalRecordService.updateMedicalRecordWithProfession(currentRecord);
            } else {
                saved = medicalRecordService.createMedicalRecord(
                        currentRecord,
                        currentAppointment.getId(),
                        loggedInDoctor.getId()
                );
            }
            this.currentRecord = saved;
            return saved;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu hồ sơ: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        MedicalRecordDTO saved = performSave();
        if (saved != null) {
            if (completeCheckBox.isSelected()) {
                try {
                    appointmentService.completeAppointment(currentAppointment.getId());
                    this.completed = true;
                } catch (Exception e) {
                    //... xử lý lỗi
                }
            }
            this.savedRecord = saved;
            closeDialog(event);
        }
    }


    @FXML
    private void handleCancel(ActionEvent event) {
        savedRecord = null;
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public MedicalRecordDTO getSavedRecord() {
        return savedRecord;
    }
    public boolean isCompleted() {
        return completed;
    }
}
